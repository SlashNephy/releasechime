package blue.starry.releasechime

import blue.starry.releasechime.db.GitHubCommitHistories
import blue.starry.releasechime.db.GitHubReleaseHistories
import blue.starry.releasechime.github.GitHubApiClient
import blue.starry.releasechime.github.GitHubCommit
import blue.starry.releasechime.github.GitHubRelease
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime

object GitHubWatcher {
    private val logger = KotlinLogging.create("App.Watcher")

    init {
        transaction(AppDatabase) {
            SchemaUtils.create(GitHubReleaseHistories)
            SchemaUtils.create(GitHubCommitHistories)
        }
    }

    suspend fun check() = coroutineScope {
        listOf(
            Env.TARGET_RELEASE_REPOSITORIES.map {
                launch {
                    checkRelease(it)
                }
            },
            Env.TARGET_COMMIT_REPOSITORIES.map {
                launch {
                    checkCommit(it)
                }
            },
            Env.TARGET_PATH_COMMIT_REPOSITORIES.map {
                launch {
                    checkPathCommit(it)
                }
            }
        ).flatten().joinAll()
    }

    private suspend fun checkRelease(repository: String) {
        val lastId = transaction(AppDatabase) {
            GitHubReleaseHistories.select { GitHubReleaseHistories.repository eq repository }
                .firstOrNull()
                ?.get(GitHubReleaseHistories.id)
        }

        val release = GitHubApiClient.getLatestRelease(repository) ?: return
        logger.trace { "Release: $release" }

        if (lastId == release.id) {
            logger.trace { "Release: Last release id \"${lastId}\" is same to latest one. Skipping..." }
            return
        }

        if (lastId != null && lastId < release.id) {
            notify(release, repository)
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                GitHubReleaseHistories.update({ GitHubReleaseHistories.repository eq repository }) {
                    it[this.id] = release.id
                }
            // insert if not exists
            } else {
                GitHubReleaseHistories.insert {
                    it[this.repository] = repository
                    it[this.id] = release.id
                }
            }
        }
    }

    private suspend fun checkCommit(repository: String) {
        val lastId = transaction(AppDatabase) {
            GitHubCommitHistories.select { GitHubCommitHistories.repository eq repository }
                .firstOrNull()
                ?.get(GitHubCommitHistories.id)
        }

        val commit = GitHubApiClient.getLatestCommit(repository) ?: return
        logger.trace { "Commit: $commit" }

        if (lastId == commit.sha) {
            logger.trace { "Commit: Last commit sha \"${lastId}\" is same to latest one. Skipping..." }
            return
        }

        if (lastId != null) {
            notify(commit, repository, null)
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                GitHubCommitHistories.update({ GitHubCommitHistories.repository eq repository }) {
                    it[this.id] = commit.sha
                }
            // insert if not exists
            } else {
                GitHubCommitHistories.insert {
                    it[this.repository] = repository
                    it[this.id] = commit.sha
                }
            }
        }
    }

    private suspend fun checkPathCommit(target: String) {
        val lastId = transaction(AppDatabase) {
            GitHubCommitHistories.select { GitHubCommitHistories.repository eq target }
                .firstOrNull()
                ?.get(GitHubCommitHistories.id)
        }
        val (repository, path) = target.split(',', limit = 2)

        val commit = GitHubApiClient.getLatestPathCommit(repository, path) ?: return
        logger.trace { "PathCommit: $commit" }

        if (lastId == commit.sha) {
            logger.trace { "PathCommit: Last commit sha \"${lastId}\" is same to latest one. Skipping..." }
            return
        }

        if (lastId != null) {
            notify(commit, repository, path)
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                GitHubCommitHistories.update({ GitHubCommitHistories.repository eq target }) {
                    it[this.id] = commit.sha
                }
                // insert if not exists
            } else {
                GitHubCommitHistories.insert {
                    it[this.repository] = target
                    it[this.id] = commit.sha
                }
            }
        }
    }

    private suspend fun notify(release: GitHubRelease, repository: String) {
        for (webhookUrl in Env.DISCORD_WEBHOOK_URLS) {
            notifyToDiscordWebhook(webhookUrl, release, repository)
        }
    }

    private suspend fun notify(commit: GitHubCommit, repository: String, path: String?) {
        for (webhookUrl in Env.DISCORD_WEBHOOK_URLS) {
            notifyToDiscordWebhook(webhookUrl, commit, repository, path)
        }
    }

    private suspend fun notifyToDiscordWebhook(webhookUrl: String, release: GitHubRelease, repository: String) {
        if (release.assets.isEmpty()) {
            logger.trace { "This release has no assets. Skipping..." }
            return
        }

        AppHttpClient.post<Unit>(webhookUrl) {
            contentType(ContentType.Application.Json)

            body = DiscordWebhookMessage(
                embeds = listOf(
                    DiscordEmbed(
                        author = DiscordEmbed.Author(
                            name = release.author.login,
                            url = release.author.htmlUrl,
                            iconUrl = release.author.avatarUrl
                        ),
                        title = "[$repository] ${release.name}",
                        url = release.htmlUrl,
                        description = release.body.let {
                            if (it.length > 300) {
                                it.take(300) + "..."
                            } else {
                                it
                            }
                        },
                        fields = release.assets.map { asset ->
                            DiscordEmbed.Field(
                                name = ":paperclip: ${asset.name} (${String.format("%.2f", asset.size / 1024.0 / 1024, 2)} MB)",
                                value = asset.browserDownloadUrl,
                                inline = false
                            )
                        },
                        timestamp = OffsetDateTime.parse(release.publishedAt).toZonedDateTime()
                    )
                )
            )
        }
    }

    private suspend fun notifyToDiscordWebhook(webhookUrl: String, commit: GitHubCommit, repository: String, path: String?) {
        AppHttpClient.post<Unit>(webhookUrl) {
            contentType(ContentType.Application.Json)

            body = DiscordWebhookMessage(
                embeds = listOf(
                    DiscordEmbed(
                        author = DiscordEmbed.Author(
                            name = commit.author.login,
                            url = commit.author.htmlUrl,
                            iconUrl = commit.author.avatarUrl
                        ),
                        title = if (path != null) "[$repository] New commits in $path" else "[$repository] New commits",
                        url = commit.htmlUrl,
                        description = commit.commit.message,
                        timestamp = OffsetDateTime.parse(commit.commit.author.date).toZonedDateTime()
                    )
                )
            )
        }
    }
}

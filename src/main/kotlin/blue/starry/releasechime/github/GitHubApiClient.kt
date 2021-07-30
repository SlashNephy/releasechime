package blue.starry.releasechime.github

import blue.starry.releasechime.AppHttpClient
import blue.starry.releasechime.Env
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

object GitHubApiClient {
    private suspend fun getReleases(repository: String, count: Int): List<GitHubRelease> {
        return AppHttpClient.get("https://api.github.com/repos/${repository}/releases?per_page=${count}") {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
    }

    suspend fun getLatestRelease(repository: String): GitHubRelease? {
        return getReleases(repository, 1).firstOrNull()
    }

    private suspend fun getCommits(repository: String, count: Int): List<GitHubCommit> {
        return AppHttpClient.get("https://api.github.com/repos/${repository}/commits?per_page=${count}") {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
    }

    suspend fun getLatestCommit(repository: String): GitHubCommit? {
        return getCommits(repository, 1).firstOrNull()
    }

    private suspend fun getPathCommits(repository: String, path: String, count: Int): List<GitHubCommit> {
        return AppHttpClient.get("https://api.github.com/repos/${repository}/commits?path=${path}&per_page=${count}") {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
    }

    suspend fun getLatestPathCommit(repository: String, path: String): GitHubCommit? {
        return getPathCommits(repository, path, 1).firstOrNull()
    }
}

package blue.starry.releasechime.github

import blue.starry.releasechime.AppHttpClient
import blue.starry.releasechime.Env
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

object GitHubApiClient {
    private suspend fun getReleases(repository: String, count: Int): List<GitHubRelease> {
        val response = AppHttpClient.get("https://api.github.com/repos/${repository}/releases?per_page=${count}") {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
        return response.body()
    }

    suspend fun getLatestRelease(repository: String): GitHubRelease? {
        return getReleases(repository, 1).firstOrNull()
    }

    private suspend fun getCommits(repository: String, count: Int): List<GitHubCommit> {
        val url = if (':' in repository) {
            val (repo, branch) = repository.split(':', limit = 2)
            "https://api.github.com/repos/${repo}/commits?sha=${branch}&per_page=${count}"
        } else {
            "https://api.github.com/repos/${repository}/commits?per_page=${count}"
        }

        val response = AppHttpClient.get(url) {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
        return response.body()
    }

    suspend fun getLatestCommit(repository: String): GitHubCommit? {
        return getCommits(repository, 1).firstOrNull()
    }

    private suspend fun getPathCommits(repository: String, path: String, count: Int): List<GitHubCommit> {
        val url = if (':' in repository) {
            val (repo, branch) = repository.split(':', limit = 2)
            "https://api.github.com/repos/${repo}/commits?sha=${branch}&path=${path}&per_page=${count}"
        } else {
            "https://api.github.com/repos/${repository}/commits?path=${path}&per_page=${count}"
        }

        val response = AppHttpClient.get(url) {
            header(HttpHeaders.Authorization, "token ${Env.GITHUB_TOKEN}")
        }
        return response.body()
    }

    suspend fun getLatestPathCommit(repository: String, path: String): GitHubCommit? {
        return getPathCommits(repository, path, 1).firstOrNull()
    }
}

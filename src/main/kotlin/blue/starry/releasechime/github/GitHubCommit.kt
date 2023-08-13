package blue.starry.releasechime.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubCommit(
    val author: Author,
    @SerialName("comments_url")
    val commentsUrl: String,
    val commit: Commit,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("node_id")
    val nodeId: String,
    val parents: List<Parent>,
    val sha: String,
    val url: String
) {
    @Serializable
    data class Author(
        @SerialName("avatar_url")
        val avatarUrl: String,
        @SerialName("events_url")
        val eventsUrl: String,
        @SerialName("followers_url")
        val followersUrl: String,
        @SerialName("following_url")
        val followingUrl: String,
        @SerialName("gists_url")
        val gistsUrl: String,
        @SerialName("gravatar_id")
        val gravatarId: String,
        @SerialName("html_url")
        val htmlUrl: String,
        val id: Int,
        val login: String,
        @SerialName("node_id")
        val nodeId: String,
        @SerialName("organizations_url")
        val organizationsUrl: String,
        @SerialName("received_events_url")
        val receivedEventsUrl: String,
        @SerialName("repos_url")
        val reposUrl: String,
        @SerialName("site_admin")
        val siteAdmin: Boolean,
        @SerialName("starred_url")
        val starredUrl: String,
        @SerialName("subscriptions_url")
        val subscriptionsUrl: String,
        val type: String,
        val url: String
    )

    @Serializable
    data class Commit(
        val author: Author,
        @SerialName("comment_count")
        val commentCount: Int,
        val committer: Committer,
        val message: String,
        val tree: Tree,
        val url: String
    ) {
        @Serializable
        data class Author(
            val date: String,
            val email: String,
            val name: String
        )

        @Serializable
        data class Committer(
            val date: String,
            val email: String,
            val name: String
        )

        @Serializable
        data class Tree(
            val sha: String,
            val url: String
        )
    }

    @Serializable
    data class Parent(
        @SerialName("html_url")
        val htmlUrl: String,
        val sha: String,
        val url: String
    )
}

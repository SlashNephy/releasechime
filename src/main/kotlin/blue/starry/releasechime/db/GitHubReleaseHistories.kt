package blue.starry.releasechime.db

import org.jetbrains.exposed.sql.Table

object GitHubReleaseHistories: Table() {
    val repository = varchar("repository", 64)
    val id = integer("id")
}

package blue.starry.releasechime.db

import org.jetbrains.exposed.sql.Table

object GitHubCommitHistories: Table() {
    val repository = varchar("repository", 64)
    val id = varchar("id", 64)
}

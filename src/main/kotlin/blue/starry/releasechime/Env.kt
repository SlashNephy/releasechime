package blue.starry.releasechime

import kotlin.properties.ReadOnlyProperty

object Env {
    val DISCORD_WEBHOOK_URLS by stringList
    val INTERVAL_SEC by long { 1800 }
    val LOG_LEVEL by stringOrNull
    val GITHUB_TOKEN by string
    val TARGET_RELEASE_REPOSITORIES by stringList
    val TARGET_COMMIT_REPOSITORIES by stringList
    val TARGET_PATH_COMMIT_REPOSITORIES by stringList
    val CACHE_DIR by stringOrNull
}

private val string: ReadOnlyProperty<Env, String>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name) ?: error("Env: ${property.name} is not present.")
    }

private val stringOrNull: ReadOnlyProperty<Env, String?>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)
    }

private val stringList: ReadOnlyProperty<Env, List<String>>
    get() = ReadOnlyProperty { _, property ->
        System.getenv().filter { it.key.startsWith(property.name) }.values.toList()
    }

private fun long(default: () -> Long): ReadOnlyProperty<Env, Long> {
    return ReadOnlyProperty { _, property ->
        System.getenv(property.name)?.toLongOrNull() ?: default()
    }
}

package blue.starry.releasechime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration

private val logger = KotlinLogging.create("app")

suspend fun main() {
    logger.info { "Application started!" }

    while (true) {
        GitHubWatcher.check()

        val sleep = Duration.seconds(Env.INTERVAL_SEC)
        logger.trace { "Sleep ${sleep}." }
        delay(sleep)
    }
}

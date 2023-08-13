package blue.starry.releasechime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

private val logger = KotlinLogging.create("app")

suspend fun main() {
    logger.info { "Application started!" }

    while (true) {
        val time = measureTime {
            GitHubWatcher.check()
        }

        val sleep = Env.INTERVAL_SEC.seconds
        logger.trace { "Operation was done in $time. Sleep $sleep." }
        delay(sleep)
    }
}

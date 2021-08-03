package blue.starry.releasechime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration
import kotlin.time.measureTime

private val logger = KotlinLogging.create("app")

suspend fun main() {
    logger.info { "Application started!" }

    while (true) {
        val time = measureTime {
            GitHubWatcher.check()
        }

        val sleep = Duration.seconds(Env.INTERVAL_SEC)
        logger.trace { "Operation was done in $time. Sleep $sleep." }
        delay(sleep)
    }
}

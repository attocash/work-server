package cash.atto.work

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.commons.AttoWorker
import cash.atto.commons.toHex
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

@Component
class Worker(
    attoWorkers: List<AttoWorker>,
) {
    private val logger = KotlinLogging.logger {}

    private val pool =
        Channel<AttoWorker>(capacity = attoWorkers.size).apply {
            runBlocking {
                attoWorkers.forEach { worker -> send(worker) }
            }
        }

    suspend fun calculate(
        network: AttoNetwork,
        timestamp: Instant,
        target: ByteArray,
    ): AttoWork {
        val start = Clock.System.now()

        logger.info { "Waiting for worker..." }

        val worker = pool.receive()

        logger.info { "Worker acquired and started for $timestamp ${target.toHex()} from $network network" }

        try {
            val work = worker.work(network, timestamp, target)
            val duration = Clock.System.now() - start
            logger.info { "Work for $timestamp ${target.toHex()} from $network network completed, duration: $duration" }
            return work
        } finally {
            pool.send(worker)
        }
    }
}

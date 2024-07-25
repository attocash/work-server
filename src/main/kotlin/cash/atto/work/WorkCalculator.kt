package cash.atto.work

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.commons.AttoWorker
import cash.atto.commons.opencl
import cash.atto.work.adapter.ApplicationProperties
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

@Component
class WorkCalculator(
    private val properties: ApplicationProperties,
) {
    private val workerPool =
        Channel<AttoWorker>(capacity = properties.queueSize).apply {
            repeat(properties.queueSize) {
                runBlocking {
                    send(AttoWorker.opencl(properties.device.toUByte()))
                }
            }
        }

    suspend fun calculate(
        network: AttoNetwork,
        timestamp: Instant,
        target: ByteArray,
    ): AttoWork {
        val worker = workerPool.receive()
        try {
            return worker.work(network, timestamp, target)
        } finally {
            workerPool.send(worker)
        }
    }
}

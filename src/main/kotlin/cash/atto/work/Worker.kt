package cash.atto.work

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.commons.AttoWorker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component

@Component
class Worker(
    attoWorkers: List<AttoWorker>,
) {
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
        val worker = pool.receive()
        try {
            return worker.work(network, timestamp, target)
        } finally {
            pool.send(worker)
        }
    }
}

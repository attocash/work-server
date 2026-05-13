package cash.atto.work

import cash.atto.commons.AttoAlgorithm
import cash.atto.commons.AttoAmount
import cash.atto.commons.AttoHash
import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoPublicKey
import cash.atto.commons.AttoWork
import cash.atto.commons.toAttoVersion
import cash.atto.commons.worker.AttoWorker
import cash.atto.commons.worker.remote
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient
import kotlin.random.Random

class WorkStepDefinition(
    private val webClient: WebClient,
) {
    private val logger = KotlinLogging.logger {}

    @LocalServerPort
    var port: Int = 0

    @Given("{day}'s block {word}")
    fun createBlock(
        day: Day,
        shortKey: String,
    ) {
        val publicKey = AttoPublicKey(Random.nextBytes(ByteArray(32)))
        val block =
            AttoOpenBlock(
                network = AttoNetwork.LOCAL,
                version = 0U.toAttoVersion(),
                algorithm = AttoAlgorithm.V1,
                publicKey = publicKey,
                balance = AttoAmount.MAX,
                timestamp = day.getInstant(),
                sendHashAlgorithm = AttoAlgorithm.V1,
                sendHash = AttoHash(ByteArray(32)),
                representativeAlgorithm = AttoAlgorithm.V1,
                representativePublicKey = publicKey,
            )
        PropertyHolder.add(shortKey, block)
    }

    @When("work is requested")
    fun request() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder[AttoOpenBlock::class.java, shortKey]

        val worker = AttoWorker.remote("http://localhost:$port")

        val work = runBlocking { worker.work(block) }

        PropertyHolder.add(shortKey, work)
    }

    @Then("work is generated")
    fun assertGenerated() {
        val block = PropertyHolder[AttoOpenBlock::class.java]
        val work = PropertyHolder[AttoWork::class.java]
        assertTrue(work.isValid(block))
    }
}

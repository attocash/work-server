package cash.atto.work

import cash.atto.commons.AttoAlgorithm
import cash.atto.commons.AttoAmount
import cash.atto.commons.AttoHash
import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoPublicKey
import cash.atto.commons.toAttoVersion
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import junit.framework.TestCase.assertTrue
import mu.KotlinLogging
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
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
        val publicKey = AttoPublicKey(Random.Default.nextBytes(ByteArray(32)))
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
                representative = publicKey,
            )
        PropertyHolder.add(shortKey, block)
    }

    @When("work is requested")
    fun request() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder.get(AttoOpenBlock::class.java, shortKey)

        val request =
            WorkRequest(
                block.network,
                block.timestamp,
                block.publicKey.toString(),
            )

        val response =
            webClient
                .post()
                .uri("http://localhost:$port/works")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux<WorkResponse>()
                .doOnError { logger.error(it) { "Failed to send $request" } }
                .log()
                .blockFirst()!!
        PropertyHolder.add(shortKey, response)
    }

    @Then("work is generated")
    fun assertGenerated() {
        val block = PropertyHolder.get(AttoOpenBlock::class.java)
        val response = PropertyHolder.get(WorkResponse::class.java)
        assertTrue(response.work.isValid(block))
    }
}

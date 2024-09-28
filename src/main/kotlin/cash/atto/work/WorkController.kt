package cash.atto.work

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.commons.fromHexToByteArray
import io.swagger.v3.oas.annotations.Operation
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkController(
    private val calculator: Worker,
) {
    @PostMapping("/works", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @Operation(description = "Generate work")
    suspend fun generate(
        @RequestBody request: WorkRequest,
    ): ResponseEntity<WorkResponse> {
        if (request.timestamp > Clock.System.now()) {
            return ResponseEntity.badRequest().build()
        }
        val work = calculator.calculate(request.network, request.timestamp, request.target.fromHexToByteArray())
        return ResponseEntity.ok(WorkResponse(work))
    }
}

@Serializable
data class WorkRequest(
    val network: AttoNetwork,
    val timestamp: Instant,
    val target: String,
)

@Serializable
data class WorkResponse(
    @Contextual val work: AttoWork,
)

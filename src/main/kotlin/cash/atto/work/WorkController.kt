package cash.atto.work

import cash.atto.commons.AttoInstant
import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoWork
import cash.atto.commons.AttoWorkTarget
import cash.atto.commons.AttoWorkTargetAsStringSerializer
import io.swagger.v3.oas.annotations.Operation
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.minutes

@RestController
class WorkController(
    private val calculator: Worker,
) {
    @PostMapping("/works")
    @Operation(description = "Generate work")
    suspend fun generate(
        @RequestBody request: WorkRequest,
    ): ResponseEntity<WorkResponse> {
        if (request.timestamp > AttoInstant.now() + 1.minutes) {
            return ResponseEntity.badRequest().build()
        }
        val work = calculator.calculate(request.network, request.timestamp, request.target)
        return ResponseEntity.ok(WorkResponse(work))
    }
}

@Serializable
data class WorkRequest(
    val network: AttoNetwork,
    val timestamp: AttoInstant,
    @Serializable(with = AttoWorkTargetAsStringSerializer::class)
    val target: AttoWorkTarget,
)

@Serializable
data class WorkResponse(
    @Contextual val work: AttoWork,
)

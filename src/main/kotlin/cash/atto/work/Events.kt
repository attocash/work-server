package cash.atto.work

import cash.atto.commons.AttoHash
import cash.atto.commons.AttoWork
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class WorkRequested(
    val callbackUrl: String,
    @Contextual val hash: AttoHash,
    val threshold: ULong,
)

@Serializable
data class WorkGenerated(
    val callbackUrl: String,
    @Contextual val hash: AttoHash,
    val threshold: ULong,
    @Contextual val work: AttoWork,
)

package cash.atto.work

import io.cucumber.java.ParameterType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

enum class Day(
    private val supplier: () -> Instant,
) {
    TODAY({ Clock.System.now() }),
    TOMORROW({ Clock.System.now().plus(24.days) }),
    ;

    fun getInstant(): Instant = supplier.invoke()
}

class DayParameterType {
    @ParameterType("TODAY|TOMORROW")
    fun day(day: String): Day = Day.valueOf(day)
}

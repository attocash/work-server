package cash.atto.work

import cash.atto.commons.AttoInstant
import io.cucumber.java.ParameterType
import kotlin.time.Duration.Companion.days

enum class Day(
    private val supplier: () -> AttoInstant,
) {
    TODAY({ AttoInstant.now() }),
    TOMORROW({ AttoInstant.now().plus(1.days) }),
    ;

    fun getInstant(): AttoInstant = supplier.invoke()
}

class DayParameterType {
    @ParameterType("TODAY|TOMORROW")
    fun day(day: String): Day = Day.valueOf(day)
}

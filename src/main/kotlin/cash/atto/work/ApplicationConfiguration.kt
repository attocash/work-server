package cash.atto.work

import cash.atto.commons.AttoWorker
import cash.atto.commons.cpu
import cash.atto.commons.opencl
import cash.atto.work.adapter.ApplicationProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ApplicationConfiguration {
    private val logger = KotlinLogging.logger {}

    @Bean
    @ConditionalOnMissingBean
    @Profile("!cpu")
    fun openclWorker(properties: ApplicationProperties): List<AttoWorker> {
        return (1..properties.queueSize)
            .map {
                AttoWorker.opencl(properties.device.toUByte())
            }
    }

    @Bean
    @ConditionalOnMissingBean
    @Profile("cpu")
    fun cpuWorker(properties: ApplicationProperties): List<AttoWorker> {
        logger.warn { "Using CPU worker. This configuration is only recommended for testing purposes." }
        return listOf(AttoWorker.cpu())
    }
}

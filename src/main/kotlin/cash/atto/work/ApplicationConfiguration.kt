package cash.atto.work

import cash.atto.commons.AttoWorker
import cash.atto.commons.opencl
import cash.atto.work.adapter.ApplicationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun openclWorker(properties: ApplicationProperties): List<AttoWorker> =
        (1..properties.queueSize)
            .map {
                AttoWorker.opencl(properties.device.toUByte())
            }
}

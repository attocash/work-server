package cash.atto.work.adapter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "atto.work")
class ApplicationProperties {
    var queueSize: Int = 4
    var device: Byte = 0
}

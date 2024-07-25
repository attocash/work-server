package cash.atto.work

import cash.atto.commons.AttoWorker
import cash.atto.commons.cpu
import io.netty.handler.logging.LogLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

@Configuration
class TestApplicationConfiguration {
    @Bean
    fun cpuWorker(): List<AttoWorker> {
        return listOf(AttoWorker.cpu())
    }

    @Bean
    fun exchangeStrategies(): ExchangeStrategies {
        return ExchangeStrategies
            .builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer
                    .defaultCodecs()
                configurer
                    .defaultCodecs()
            }.build()
    }

    @Bean
    fun webClient(exchangeStrategies: ExchangeStrategies): WebClient {
        val httpClient: HttpClient =
            HttpClient
                .create()
                .wiretap(
                    this.javaClass.canonicalName,
                    LogLevel.DEBUG,
                    AdvancedByteBufFormat.TEXTUAL,
                )
        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)
        return WebClient
            .builder()
            .exchangeStrategies(exchangeStrategies)
            .clientConnector(connector)
            .build()
    }
}

package br.com.dextra.poc.sleuth.facade.config

import br.com.dextra.poc.sleuth.facade.logger.LoggerContext
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.time.Duration


@Configuration
class WebClientConfig(
    @Value("\${http.timeout}")
    private val timeout: Duration
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun webClient(): WebClient {
        return WebClient
                .builder()
                .clientConnector(ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(traceIdFilter())
                .filter(ExchangeFilterFunctions.basicAuthentication("user", "password"))
                .build()
    }

    // Filtro para enviar o X-alelo-traceid nas requisições
    fun traceIdFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction { request: ClientRequest?, next: ExchangeFunction ->
            next.exchange(
                ClientRequest.from(request)
                    .headers { headers: HttpHeaders ->
                        headers.add(LoggerContext.TRACE_ID_HEADER, MDC.get(LoggerContext.TRACE_ID))
                    }
                    .build()
            )
        }
    }

    private fun getTcpClient(): TcpClient {
        return TcpClient.create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        timeout.toMillisPart()
                )
                .doOnConnected { connection ->
                    connection.addHandlerLast(
                            ReadTimeoutHandler(timeout.toSecondsPart())
                    )
                    connection.addHandlerLast(
                            WriteTimeoutHandler(timeout.toSecondsPart()))
                }
    }
}

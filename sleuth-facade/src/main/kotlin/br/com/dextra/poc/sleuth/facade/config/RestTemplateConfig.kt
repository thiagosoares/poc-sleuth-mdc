package br.com.dextra.poc.sleuth.facade.config

import br.com.dextra.poc.sleuth.facade.logger.LoggerContext
import java.io.BufferedReader
import java.time.Duration
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Value("\${http.debug}")
    val httpDebug: Boolean? = null

    @Value("\${http.timeout}")
    val timeout: Duration? = null

    private val logger = KotlinLogging.logger {}

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        val additionalMediaTypeList = listOf(MediaType.APPLICATION_JSON)

        val additionalConverter = MappingJackson2HttpMessageConverter()
        additionalConverter.supportedMediaTypes = additionalMediaTypeList

        return builder.requestFactory {
            BufferingClientHttpRequestFactory(
                    HttpComponentsClientHttpRequestFactory()
            )
        }
            .additionalMessageConverters(additionalConverter)
            .setConnectTimeout(timeout)
            .setReadTimeout(timeout)
            .interceptors(listOf(defaultRequestInterceptor()))
            .build()
    }


    private fun defaultRequestInterceptor(): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { httpRequest: HttpRequest,
                                              bytes: ByteArray,
                                              requestExecution: ClientHttpRequestExecution ->
            httpRequest.headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            // TODO Importante: enviar o X-alelo-traceid nas requisições
            httpRequest.headers.add(LoggerContext.TRACE_ID_HEADER, MDC.get(LoggerContext.TRACE_ID))
            val response = requestExecution.execute(httpRequest, bytes)
            response
        }
    }

}

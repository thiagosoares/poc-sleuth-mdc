package br.com.dextra.poc.sleuth.facade.service

import br.com.dextra.poc.sleuth.facade.logger.LoggerContext
import mu.KotlinLogging
import mu.withLoggingContext
import org.slf4j.MDC
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.net.URI

@Service
class HelloService(
    private val restTemplate: RestTemplate,
    private val webClient: WebClient,
) {

    private val logger = KotlinLogging.logger {}

    fun hello(
        id: String
    ): HelloResponse {

        logger.info("hello: objectId=$id")

        return restTemplate
            .getForObject(
                URI("http://localhost:3009/hello-partner/"),
                HelloResponse::class.java
            )!!.also {
                logger.info("hello: Http resp ok, objectId=$id, resp=$it")
            }
    }

    fun helloReactor(
        id: String
    ): Mono<HelloResponse> {

        logger.info("helloWebClient: objectId=$id")

//        return re1(id)
//            .flatMapLogging { req2(id) }
//            .subscriberContext {
//                    ctx -> ctx.put(LoggerContext.TRACE_ID, MDC.get(LoggerContext.TRACE_ID))
//            }

//        return withMDCLogging {
//            logger.debug { "Start processing $id.." }
//        } // log first message with MDC context
//        .flatMapLogging { // use our extension
//            logger.debug { "Calling Another Service..." }
//            req1(id)
//        }
        return req1(id)
        .flatMapLogging { // use our extension
            logger.debug { "Got response from Another Service: $it. Saving to Redis..." }
            req2(id)
        }
        .mapLogging { // use our extension
            logger.debug { "Saved $it to Redis. Generating response..." }
//            toResponse(it)
            it
        }
        .subscriberContext { ctx -> ctx.put(LoggerContext.TRACE_ID, MDC.get(LoggerContext.TRACE_ID)) }

    }

    fun req1(
        id: String
    ): Mono<HelloResponse> {

        logger.info("re1: objectId=$id")

        return webClient.get()
            .uri("http://localhost:3009/hello-partner/")
            .retrieve()
            .bodyToMono(HelloResponse::class.java)
            .doOnSuccess {
                logger.info("re1: Http resp ok, " +
                    "objectId=$id, resp=$it")
            }.doOnError { error ->
                logger.error(error) {
                    "re1: Http resp error, " +
                        "objectId=$id, error=$error"
                }
                throw error
            }
    }

    fun req2(
        id: String
    ): Mono<HelloResponse> {

        logger.info("req2: objectId=$id")

        return webClient.get()
            .uri("http://localhost:3009/hello-partner/test")
            .retrieve()
            .bodyToMono(HelloResponse::class.java)
            .doOnSuccess {
                logger.info("req2: Http resp ok, " +
                    "objectId=$id, resp=$it")
            }.doOnError { error ->
                logger.error(error) {
                    "req2: Http resp error, " +
                        "objectId=$id, error=$error"
                }
                throw error
            }
    }

}

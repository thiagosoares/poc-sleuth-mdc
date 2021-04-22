package br.com.dextra.poc.sleuth.facade.service

import brave.propagation.ExtraFieldPropagation
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.util.context.Context
import java.net.URI
import java.util.function.Consumer


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

        return re1(id)
              .flatMap {
                  req2(id)
              }
//            .subscriberContext {
//                    ctx -> ctx.put(LoggerContext.TRACE_ID, ">>>>>")
//            }
    }

    fun re1(
        id: String
    ): Mono<HelloResponse> {

        logger.info("helloWebClient: objectId=$id")

        return webClient.get()
            .uri("http://localhost:3009/hello-partner/")
            .retrieve()
            .bodyToMono(HelloResponse::class.java)
            .doOnSuccess {
                logger.info("helloWebClient: Http resp ok, " +
                    "objectId=$id, resp=$it, " +
                    "Baggage: ${ExtraFieldPropagation.get("XAleloTraceId")}")
            }.doOnError { error ->
                logger.error(error) {
                    "helloWebClient: Http resp error, " +
                        "objectId=$id, error=$error"
                }
                throw error
            }
    }

    fun req2(
        id: String
    ): Mono<HelloResponse> {

        logger.info("helloWebClient: objectId=$id")

        return webClient.get()
            .uri("http://localhost:3009/hello-partner/test")
            .retrieve()
            .bodyToMono(HelloResponse::class.java)
            .doOnSuccess {
                logger.info("helloWebClient: Http resp ok, " +
                    "objectId=$id, resp=$it" +
                    "Baggage: ${ExtraFieldPropagation.get("XAleloTraceId")}")
            }.doOnError { error ->
                logger.error(error) {
                    "helloWebClient: Http resp error, " +
                        "objectId=$id, error=$error"
                }
                throw error
            }
    }


    companion object {
        private const val UID = "uid"
    }

}



data class HelloResponse(val id: String, val name: String)

package br.com.dextra.poc.sleuth.facade.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
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

        return webClient.get()
            .uri("http://localhost:3009/hello-partner/")
            .retrieve()
            .bodyToMono(HelloResponse::class.java)
            .doOnSuccess {
                logger.info("helloWebClient: Http resp ok, " +
                    "objectId=$id, resp=$it")
            }.doOnError { error ->
                logger.error(error) {
                    "helloWebClient: Http resp error, " +
                        "objectId=$id, error=$error"
                }
                throw error
            }
    }

}

data class HelloResponse(val id: String, val name: String)

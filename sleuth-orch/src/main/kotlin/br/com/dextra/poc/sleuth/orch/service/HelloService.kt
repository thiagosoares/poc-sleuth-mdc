package br.com.dextra.poc.sleuth.orch.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class HelloService(
    private val restTemplate: RestTemplate
) {

    private val logger = KotlinLogging.logger {}

    fun hello(
        id: String
    ): HelloResponse {
        logger.info("hello: objectId=$id")
        return restTemplate
            .getForObject(
                URI("http://localhost:9081/poc-api/v1/hello?id=$id"),
                HelloResponse::class.java
            )!!.also {
                logger.info("hello: ok, objectId=$id, resp=$it")
            }
    }

}

data class HelloResponse(val id: String, val name: String)

package br.com.dextra.poc.sleuth.facade.controller

import br.com.dextra.poc.sleuth.facade.service.HelloResponse
import br.com.dextra.poc.sleuth.facade.service.HelloService
import brave.Tracer
import brave.Tracing
import brave.propagation.B3Propagation
import brave.propagation.ExtraFieldPropagation
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@Api(tags = ["Hello"])
@RequestMapping("/v1/hello")
class HelloController(
    private val service: HelloService
) {

    private val logger = KotlinLogging.logger {}

//    @Autowired
//    private lateinit var tracer: Tracer

    @GetMapping
    @ApiOperation("hello")
    fun hello(
        @RequestParam("id") id: String
    ): HelloResponse {

        logger.info("hello: objectId=$id")

        return service.hello(id).also {
            logger.info("hello: ok, objectId=$id")
        }
    }

    @GetMapping("/reactor")
    @ApiOperation("hello reactor")
    fun helloReactor(
        @RequestParam("id") id: String
    ): Mono<HelloResponse> {

        logger.info("helloReactor: objectId=$id")

        return service.helloReactor(id).also {
            logger.info("hello: ok, objectId=$id")
        }
    }

}


package br.com.dextra.poc.sleuth.api.controller

import br.com.dextra.poc.sleuth.api.service.HelloResponse
import br.com.dextra.poc.sleuth.api.service.HelloService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Hello"])
@RequestMapping("/v1/hello")
class HelloController(
    private val service: HelloService
) {

    private val logger = KotlinLogging.logger {}

    @GetMapping
    @ApiOperation("hello")
    fun hello(
        @RequestParam("id") id: String
    ): HelloResponse {
        logger.info("hello: objectId=$id")
        return service.hello(id)
    }

}


package br.com.dextra.poc.sleuth.orch.controller

import br.com.dextra.poc.sleuth.orch.service.HelloResponse
import br.com.dextra.poc.sleuth.orch.service.HelloService
import brave.Span
import brave.Tracer
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
    private val service: HelloService,
    private val tracer: Tracer
) {



    private val logger = KotlinLogging.logger {}

    @GetMapping
    @ApiOperation("hello")
    fun hello(
        @RequestParam("id") id: String
    ): HelloResponse {

        val span: Span = tracer.nextSpan().name("encode").start()
        span.tag("TEste", "OK")
        try {
            tracer.withSpanInScope(span).use {
                return service.hello(id)
            }
        } catch (e: RuntimeException) {
            span.error(e) // Unless you handle exceptions, you might not know the operation failed!
            throw e
        } catch (e: Error) {
            span.error(e)
            throw e
        } finally {
            span.finish() // note the scope is independent of the span. Always finish a span.
        }

//        logger.info("hello: objectId=$id")
//        return service.hello(id)
    }

}


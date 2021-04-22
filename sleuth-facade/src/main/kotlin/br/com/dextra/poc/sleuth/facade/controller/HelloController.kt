package br.com.dextra.poc.sleuth.facade.controller

import br.com.dextra.poc.sleuth.facade.logger.LoggerContext
import br.com.dextra.poc.sleuth.facade.service.HelloResponse
import br.com.dextra.poc.sleuth.facade.service.HelloService
import brave.propagation.ExtraFieldPropagation
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletRequest

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

       /* LoggerContext.initialize(
            LoggerContext.OPERATION_ID, LoggerContext.generateUUID()
        ).use {
            return service.hello(id).also {
                logger.info("hello: objectId=$id")
            }
        }*/

        logger.info("hello: objectId=$id")

        return service.hello(id).also {
            logger.info("hello: ok, objectId=$id")
        }
    }

    @GetMapping("/reactor")
    @ApiOperation("hello reactor")
    fun helloReactor(
        @RequestParam("id") id: String,
        request: HttpServletRequest
    ): Mono<HelloResponse> {
        LoggerContext.initialize(LoggerContext.TRACE_ID, getTraceId(request)).use {
            logger.info("helloReactor: objectId=$id")
            return service.helloReactor(id).also {
                logger.info("hello: ok, objectId=$id, " +
                    "${MDC.get("AleloTraceId")}"+
                    "${ExtraFieldPropagation.get("XAleloTraceId")}")
            }
        }
    }

    private fun getTraceId(httpRequest: HttpServletRequest) =
        httpRequest.getHeader(LoggerContext.TRACE_ID_HEADER) ?: LoggerContext.generateUUID()

}


package br.com.dextra.poc.sleuth.facade.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context

import javax.servlet.http.HttpServletRequest


//@Configuration
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class ReactiveRequestContextFilter/*: WebFilter*/ {

    /*fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void?>? {
        val request: HttpServletRequest = exchange.request
        return chain.filter(exchange)
            .subscriberContext { ctx: Context ->
                ctx.put(
                    ReactiveRequestContextHolder.CONTEXT_KEY,
                    request
                )
            }
    }*/
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

//        val request = exchange.request
//        return chain.filter(exchange)
//            .subscriberContext { ctx: Context ->
//                ctx.put(
//                    ReactiveRequestContextHolder.CONTEXT_KEY,
//                    request
//                )
//            }
//    }

}
package br.com.dextra.poc.sleuth.facade.config

//import org.slf4j.MDC
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
//import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.reactive.function.client.ClientRequest
//import org.springframework.web.reactive.function.client.ClientResponse
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction
//import org.springframework.web.reactive.function.client.ExchangeFunction
//import org.springframework.web.reactive.function.client.WebClient
//import reactor.core.publisher.Mono
//import reactor.core.scheduler.Schedulers


//@Configuration
//@ConditionalOnClass(WebClient::class)
class WebClientLoggingAutoConfiguration/*: WebClientCustomizer*/ {

   /* init  {
        Schedulers.onScheduleHook("mdc") { runnable: Runnable ->
            val map =
                MDC.getCopyOfContextMap()
            Runnable {
                if (map != null) {
                    MDC.setContextMap(map)
                }
                try {
                    runnable.run()
                } finally {
                }
            }
        }
    }

    override fun customize(webClientBuilder: WebClient.Builder) {
        webClientBuilder.filter(mdcFilter)
                        .filter(logRequest)
                        .filter(logResponse)
    }

    var logRequest: ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
//        println("MDC=" + MDC.getCopyOfContextMap())
        Mono.just(clientRequest)
    }

    var logResponse: ExchangeFilterFunction = ExchangeFilterFunction.ofResponseProcessor { clientResponse: ClientResponse ->
//        println("MDC=" + MDC.getCopyOfContextMap())
        Mono.just(clientResponse)
    }

    var mdcFilter: ExchangeFilterFunction = ExchangeFilterFunction { request: ClientRequest?, next: ExchangeFunction ->
        // here runs on main(request's) thread
        val map =
            MDC.getCopyOfContextMap()
        next.exchange(request)
            .doOnRequest { value: Long ->
                // here runs on reactor's thread
                if (map != null) {
                    MDC.setContextMap(map)
                }
            }
    }*/

}
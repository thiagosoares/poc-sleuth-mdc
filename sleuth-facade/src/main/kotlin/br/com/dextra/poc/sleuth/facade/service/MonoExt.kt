package br.com.dextra.poc.sleuth.facade.service

import br.com.dextra.poc.sleuth.facade.logger.LoggerContext
import mu.withLoggingContext
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.core.publisher.toMono
import reactor.util.context.Context
import reactor.kotlin.core.publisher.toMono

data class HelloResponse(val id: String, val name: String)

fun extractLoggingContext(ctx: Context) = mapOf(LoggerContext.TRACE_ID to ctx.get<String>(LoggerContext.TRACE_ID))

fun withMDCLogging(block: () -> Unit): Mono<Void> {
    return Mono.subscriberContext()
        .map { ctx ->
            val context = extractLoggingContext(ctx)
            withLoggingContext(context) {
                block()
            }
        }
        .then()
}

fun <T, R> Mono<T>.mapLogging(mapper: (T) -> R): Mono<R> {
    return handle { element, sink ->
        val context = extractLoggingContext(sink.currentContext())
        withLoggingContext(context) {
            sink.next(mapper(element))
        }
    }
}

fun <T, R> Mono<T>.flatMapLogging(mapper: (T) -> Mono<R>): Mono<R> {
    return materialize()
        .flatMap { signal ->
            if (signal.isOnNext) {
                val context = extractLoggingContext(signal.context)
                withLoggingContext(context) {
                    mapper(signal.get()!!) // get()!! is safe for OnNext signals
                        .map { signal.withNewElement(it) }
                }
            } else {
                signal.toMono()
            }
        }
        .dematerialize()
}

fun <T, R> Signal<T>.withNewElement(element: R): Signal<R> {
    return Signal.next(element, context)
}
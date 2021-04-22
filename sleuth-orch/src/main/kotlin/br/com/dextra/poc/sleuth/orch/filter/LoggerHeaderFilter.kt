package br.com.dextra.poc.sleuth.orch.filter

import org.slf4j.MDC
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggerHeaderFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        LoggerContext.initialize(LoggerContext.TRACE_ID, getTraceId(httpRequest)).use {
//            httpResponse.setHeader(LoggerContext.TRACE_ID_HEADER, MDC.get(LoggerContext.TRACE_ID))
            chain.doFilter(request, httpResponse)
        }

    }

    private fun getTraceId(httpRequest: HttpServletRequest) =
        httpRequest.getHeader(LoggerContext.TRACE_ID_HEADER) ?: LoggerContext.generateUUID()

}
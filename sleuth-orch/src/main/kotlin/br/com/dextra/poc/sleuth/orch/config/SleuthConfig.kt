package br.com.dextra.poc.sleuth.orch.config

import brave.propagation.B3Propagation
import brave.propagation.ExtraFieldPropagation
import brave.propagation.Propagation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SleuthConfig {

    @Bean
    fun propagationFactory(): Propagation.Factory? {
        return ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "my-custom-key")
    }
}

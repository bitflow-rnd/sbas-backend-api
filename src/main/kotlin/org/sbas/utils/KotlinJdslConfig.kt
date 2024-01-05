package org.sbas.utils

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class KotlinJdslConfig {

    @Produces
    fun jpqlRenderContext(): JpqlRenderContext {
        return JpqlRenderContext()
    }
}
package org.sbas.utils

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

@ApplicationScoped
class KotlinJdslConfig {

    @Inject
    private lateinit var entityManager: EntityManager
    private lateinit var queryFactory: QueryFactory

    @Produces
    fun initialize(): QueryFactory {
        queryFactory = QueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager),
            subqueryCreator = SubqueryCreatorImpl()
        )
        return queryFactory
    }

    @Produces
    fun jpqlRenderContext(): JpqlRenderContext {
        return JpqlRenderContext()
    }
}
package org.sbas.utils

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.inject.Inject
import javax.persistence.EntityManager

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
}
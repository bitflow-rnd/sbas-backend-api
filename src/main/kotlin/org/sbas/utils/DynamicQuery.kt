package org.sbas.utils

import com.fasterxml.jackson.databind.ObjectMapper
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate

@RequestScoped
class DynamicQuery {

    @Inject
    private lateinit var entityManager: EntityManager

    fun makeWith(entity: Any, param: Any): TypedQuery<out Any>? {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery: CriteriaQuery<*>

        // entity 가 Entity 클래스인지 확인
        val metamodel = entityManager.metamodel
        val findEntity = metamodel.entities.find {
            it.javaType == entity::class.java
        }

        // entity 가 엔티티가 맞으면 아래 코드 수행
        if (findEntity != null) {
            criteriaQuery = criteriaBuilder.createQuery(findEntity.javaType)
            val root = criteriaQuery.from(findEntity.javaType)
            val predicateList = mutableListOf<Predicate>()

            // param 객체를 Map 으로 변환 -> 하나씩 꺼내서 predicateList 에 추가
            val objectMapper = ObjectMapper().convertValue(param, Map::class.java)
            objectMapper.entries.forEach {
                if (it.value != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<String>(it.key.toString()), it.value))
                }
            }

            // predicateList 가 비어있지 않으면 where 추가
            if (predicateList.isNotEmpty()) {
                criteriaQuery.where(*predicateList.toTypedArray())
            }

            return entityManager.createQuery(criteriaQuery)
        }

        return null
    }
}
package org.sbas.utils

import org.eclipse.microprofile.jwt.JsonWebToken
import org.sbas.entities.CommonEntity
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@ApplicationScoped
class CommonEntityListener {

    @Inject
    private lateinit var jsonWebToken: JsonWebToken

    @PrePersist
    fun prePersist(entity: CommonEntity) {
        // Entity가 영속성 컨텍스트에 저장되기 전에 호출됩니다.
        // 이전에 수행할 로직을 여기에 작성합니다.
        if (jsonWebToken.name != null) {
            entity.rgstUserId = jsonWebToken.name
            entity.updtUserId = jsonWebToken.name
        }
    }

    @PreUpdate
    fun preUpdate(entity: CommonEntity) {
        // Entity가 업데이트되기 전에 호출됩니다.
        // 이전에 수행할 로직을 여기에 작성합니다.
        if (jsonWebToken.name != null) {
            entity.updtUserId = jsonWebToken.name
        }
    }
}
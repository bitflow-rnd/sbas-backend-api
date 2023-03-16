package org.sbas.services.admin

import org.jboss.logging.Logger
import org.sbas.entities.info.InfoUser
import org.sbas.repositories.UserInfoRepository
import org.sbas.response.StringResponse
import org.sbas.utils.CypherUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class AdminUserService {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var repository: UserInfoRepository

    @Transactional
    fun reg(infoUser: InfoUser): StringResponse {
        infoUser.pw = CypherUtils.crypto(infoUser.pw!!)

        infoUser.rgstUserId = "method76"
        infoUser.updtUserId = "method76"

        repository.persist(infoUser)

        return StringResponse(infoUser.id)
    }

}
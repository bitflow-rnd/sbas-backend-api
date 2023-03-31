package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class TestUserService {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var jwt: JsonWebToken

    @Transactional
    fun getUser(): JsonWebToken{
        return jwt
    }
    
}
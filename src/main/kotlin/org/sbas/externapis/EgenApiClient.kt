package org.sbas.externapis

import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.sbas.entities.BaseCodeId
import org.sbas.parameters.BaseCodeRequest
import org.sbas.repositories.BaseCodeRepository
import org.sbas.response.BaseCodeResponse
import org.sbas.utils.TokenUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.SecurityContext


/**
 * E-GEN API를 처리하는 클라이언트
 */
@ApplicationScoped
class EgenApiClient {

    @Inject
    lateinit var log: Logger

    
}
package org.sbas.externapis

import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


/**
 * E-GEN API를 처리하는 클라이언트
 */
@ApplicationScoped
class EgenApiClient {

    @Inject
    lateinit var log: Logger

}
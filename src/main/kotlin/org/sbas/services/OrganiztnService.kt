package org.sbas.services

import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class OrganiztnService {

    @Inject
    lateinit var log: Logger

}
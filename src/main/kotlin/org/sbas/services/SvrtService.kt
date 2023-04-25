package org.sbas.services

import org.sbas.repositories.SvrtPtRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SvrtService {

    @Inject
    private lateinit var svrtPtRepository: SvrtPtRepository

    fun saveSevrInfo() {
//        svrtPtRepository.persist()
    }
}
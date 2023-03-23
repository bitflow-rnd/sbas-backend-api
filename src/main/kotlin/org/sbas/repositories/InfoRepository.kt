package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoCrew
import org.sbas.entities.info.InfoCrewId
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoPt
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {
    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = ?1 AND rrno_1 = ?2 AND rrno_2 = ?3", ptNm, rrno1, rrno2).firstResult()
}

@ApplicationScoped
class InfoCrewRepository : PanacheRepositoryBase<InfoCrew, InfoCrewId>

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String>
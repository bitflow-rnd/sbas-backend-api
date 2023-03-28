package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.*
import org.sbas.parameters.InstCdParameters
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = ?1 AND rrno_1 = ?2 AND rrno_2 = ?3", ptNm, rrno1, rrno2).firstResult()
}

@ApplicationScoped
class InfoCrewRepository : PanacheRepositoryBase<InfoCrew, InfoCrewId> {
    fun findInfoCrews(instId: String) = find("inst_id = ?1", instId).list()
}

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String>

@ApplicationScoped
class InfoInstRepository : PanacheRepositoryBase<InfoInst, String> {

    fun findInstCodeList(param: InstCdParameters) =
        find("dstr_cd_1 = ?1 AND dstr_cd_2 = ?2 AND inst_type_cd = ?3", param.dstrCd1, param.dstrCd2, param.instTypeCd).list()

    fun findFireStatns(param: InstCdParameters) =
        find("inst_type_cd = 'ORGN0002' and dstr_cd_1 = ?1 and dstr_cd_2 = ?2", param.dstrCd1, param.dstrCd2).list()
}
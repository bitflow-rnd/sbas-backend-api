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
        find(
            "select i from InfoInst i where " +
                "('${param.dstrCd1}' = 'null' or i.dstrCd1 = '${param.dstrCd1}') and " +
                "('${param.dstrCd2}' = 'null' or i.dstrCd2 = '${param.dstrCd2}') and " +
                "('${param.instTypeCd}' = 'null' or i.instTypeCd = '${param.instTypeCd}')"
        ).list()

}
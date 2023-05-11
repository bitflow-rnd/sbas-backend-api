package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.jboss.logging.Logger
import org.sbas.dtos.SidoSiGunGuDto
import org.sbas.entities.base.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    @Inject
    lateinit var log: Logger

    fun findBaseCodeByCdGrpId(cdGrpId: String): List<BaseCode> = find("cd_grp_id = '$cdGrpId'").list()

    fun findBaseCdGrpList(): List<BaseCode> {
        return find("select a.id.cdGrpId, a.cdGrpNm from BaseCode a group by a.id.cdGrpId, a.cdGrpNm",
            Sort.by("a.id.cdGrpId")).list()
    }

    fun findCdIdByAddrNm(addrNm: String) : SidoSiGunGuDto {
        val arrAddr = addrNm.split(" ")

        val siDo = find("from BaseCode where id.cdGrpId = 'SIDO' and cdNm = '${arrAddr[0]}'").firstResult()
        val siDoCd = siDo?.id?.cdId

        val siGunGu = find("from BaseCode where id.cdGrpId = 'SIDO$siDoCd' and cdNm = '${arrAddr[1]}'").firstResult()

        return SidoSiGunGuDto(siDoCd, siGunGu?.id?.cdId)
    }

}

@ApplicationScoped
class BaseCodeEgenRepository : PanacheRepositoryBase<BaseCodeEgen, BaseCodeEgenId> {

    fun findCodeEgenByCmMid(cmMid: String): List<BaseCodeEgen> {
        return find("cm_mid = '$cmMid'").list()
    }
}

@ApplicationScoped
class BaseAttcRepository : PanacheRepositoryBase<BaseAttc, String> {
    fun deleteByAttcId(attcId: String): Long {
        return delete("attc_id = '$attcId'")
    }

    fun findByAttcId(attcId: String) = find("select ba from BaseAttc ba where ba.attcId = '$attcId'").firstResult()
}
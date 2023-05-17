package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.dtos.SidoSiGunGuDto
import org.sbas.entities.base.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    fun findBaseCodeByCdGrpId(cdGrpId: String): List<BaseCode> = find("cd_grp_id = '$cdGrpId'").list()

    fun findBaseCdGrpList(): List<BaseCode> {
        return find("select a.id.cdGrpId, a.cdGrpNm from BaseCode a group by a.id.cdGrpId, a.cdGrpNm",
            Sort.by("a.id.cdGrpId")).list()
    }

    @Transactional
    fun findCdIdByAddrNm(addrNm: String): SidoSiGunGuDto {
        val arrAddr = addrNm.split(" ")

        val siDo = find("from BaseCode where id.cdGrpId = 'SIDO' and cdNm = '${arrAddr[0]}'").firstResult()
        val siDoCd = siDo?.id?.cdId

        val siGunGu = find("from BaseCode where id.cdGrpId = 'SIDO$siDoCd' and cdNm = '${arrAddr[1]}'").firstResult()

        return SidoSiGunGuDto(siDoCd, siGunGu?.id?.cdId)
    }

    fun findByDstr1CdAndCdNm(dstr1Cd: String, cdNm: String): BaseCode? {
        return find("cd_grp_id = 'SIDO$dstr1Cd' and cd_nm = '$cdNm'").firstResult()
    }

    fun getCdNm(grpId: String, cdId: String): String {
        val findCode = find("from BaseCode where id.cdGrpId='$grpId' and id.cdId='$cdId'").firstResult()

        if(findCode == null) return ""
        else return findCode.cdNm ?: ""
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
package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.dtos.SidoSiGunGuDto
import org.sbas.entities.base.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    fun findBaseCodeGrp(cdGrpId: String): BaseCode? {
        return find("cd_grp_id = '$cdGrpId' and cd_seq = 0 and cd_grp_id = cd_id").firstResult()
    }

    fun findBaseCodeGrpList(): MutableList<Any?> {
        val query = "select new map(a.id.cdGrpId as cdGrpId, a.cdGrpNm as cdGrpNm, a.rmk as rmk) from BaseCode a " +
                "where a.id.cdGrpId = a.id.cdId and cdSeq = 0 order by a.id.cdGrpId"
        return getEntityManager().createQuery(query).resultList
    }

    fun findBaseCodeByCdGrpId(cdGrpId: String): MutableList<BaseCode> {
        return find("cd_grp_id = '$cdGrpId' and cd_seq != 0", Sort.by("cd_id", "cd_seq")).list().toMutableList()
    }

    fun findBaseCodeByCdId(cdId: String): BaseCode? {
        return find("cd_id = '$cdId'").firstResult()
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

    fun getDstrCd2Nm(dstrCd1: String?, dstrCd2: String?): String {
        val query = "select fn_get_dstr_cd2_nm('$dstrCd1', '$dstrCd2') as test"
        return getEntityManager().createNativeQuery(query).singleResult as String
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

    fun findFilesByAttcGrpId(attcGrpId: String): List<BaseAttc> {
        return find("attcGrpId = '$attcGrpId'").list()
    }

    fun findByAttcId(attcId: String) = find("select ba from BaseAttc ba where ba.attcId = '$attcId'").firstResult()

    fun findByAttcGrpIdAndAttcId(attcGrpId: String, attcId: String): BaseAttc? {
        return find("attcGrpId = '$attcGrpId' and attcId = '$attcId'").firstResult()
    }

    fun getNextValAttcGrpId(): String {
        val nextValue = getEntityManager().createNativeQuery("select nextval('base_attc_grp_seq')").singleResult
        return "AT" + nextValue.toString().padStart(8, '0')
    }

    fun getCurrValAttcGrpId(): String {
        val currentValue = getEntityManager().createNativeQuery("select currval('base_attc_grp_seq')").singleResult
        return "AT" + currentValue.toString().padStart(8, '0')
    }
}
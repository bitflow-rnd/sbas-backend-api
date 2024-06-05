package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.dtos.SidoSiGunGuDto
import org.sbas.entities.base.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException

@ApplicationScoped
class BaseCodeRepository : PanacheRepositoryBase<BaseCode, BaseCodeId> {

    fun findBaseCodeGrpList(): List<BaseCode>? {
        return find("id.cdGrpId = id.cdId and cdSeq = 0", Sort.by("id.cdGrpId")).list()
    }

    fun findAllByCdGrpId(cdGrpId: String): List<BaseCode> {
        return find("id.cdGrpId = '$cdGrpId' and cdSeq != 0", Sort.by("cdSeq", "id.cdId")).list()
    }

    fun findByCdId(cdId: String): BaseCode? {
        return find("id.cdId = '$cdId'").firstResult()
    }

    @Transactional
    fun findCdIdByAddrNm(addrNm: String): SidoSiGunGuDto {
        val arrAddr = addrNm.split(" ")

        val siDo = find("from BaseCode where id.cdGrpId = 'SIDO' and cdNm = '${arrAddr[0]}'").firstResult()
        val siDoCd = siDo?.id?.cdId

        val siGunGu = find("from BaseCode where id.cdGrpId = 'SIDO$siDoCd' and cdNm = '${arrAddr[1]}'").firstResult()

        return SidoSiGunGuDto(siDoCd, siGunGu?.id?.cdId)
    }

    fun findByDstr1CdAndCdNm(dstr1Cd: String, cdNm: String): BaseCode {
        return find("id.cdGrpId = 'SIDO$dstr1Cd' and cdNm = '$cdNm'").firstResult() ?: throw NotFoundException("baseCode not found")
    }

    fun getdstr2CdNm(dstr1Cd: String?, dstr2Cd: String?): String {
        val query = "select fn_get_dstr_cd2_nm('$dstr1Cd', '$dstr2Cd') as test"
        return getEntityManager().createNativeQuery(query).singleResult as String
    }
}

@ApplicationScoped
class BaseCodeEgenRepository : PanacheRepositoryBase<BaseCodeEgen, BaseCodeEgenId> {

    fun findCodeEgenByCmMid(cmMid: String): List<BaseCodeEgen> {
        return find("id.cmMid = '$cmMid'").list()
    }
}

@ApplicationScoped
class BaseAttcRepository : PanacheRepositoryBase<BaseAttc, String> {
    fun deleteByAttcId(attcId: String): Long? {
        return delete("attcId = '$attcId'")
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
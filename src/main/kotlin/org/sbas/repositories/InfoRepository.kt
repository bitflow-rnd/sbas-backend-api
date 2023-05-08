package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.dtos.InfoPtSearchDto
import org.sbas.entities.info.*
import org.sbas.parameters.InstCdParameters
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = ?1 AND rrno_1 = ?2 AND rrno_2 = ?3", ptNm, rrno1, rrno2).firstResult()

    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr_1_cd = '$dstr1Cd' and dstr_2_cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(): List<InfoPtSearchDto> {
        val query = "select new org.sbas.dtos.InfoPtSearchDto(a.ptId, b.id.bdasSeq, a.ptNm, a.gndr, " +
                "a.dstr1Cd, (select cdNm from BaseCode where id.cdId = a.dstr1Cd), a.dstr2Cd, (select cdNm from BaseCode where id.cdId = a.dstr2Cd), " +
                "ba.hospId, '', a.mpno, a.natiCd, a.bedStatCd, a.bedStatNm, b.updtDttm, " +
                "b.ptTypeCd, b.svrtTypeCd, b.undrDsesCd, ba.admsStatCd, ba.admsStatNm, " +
                "EXTRACT(year FROM age(CURRENT_DATE, to_date(case a.rrno2 when '3' then concat('20',a.rrno1) when '4' then concat('20',a.rrno1) else concat('19',a.rrno1) end, 'yyyyMMdd')))) " +
                "FROM InfoPt a " +
                "inner join BdasReq b on a.ptId = b.id.ptId " +
                "left outer join BdasAdms ba on b.id.bdasSeq = ba.id.bdasSeq " +
                "where b.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
                "order by b.updtDttm desc"
//        val query = "select a.pt_id, b.bdas_seq, a.pt_nm, a.gndr, " +
//                "a.dstr_1_cd, a.dstr_2_cd, ba.hosp_id, '', a.mpno, a.nati_cd, a.bed_stat_cd, a.bed_stat_nm, b.updt_dttm, " +
//                "b.pt_type_cd, b.svrt_type_cd, b.undr_dses_cd, ba.adms_stat_cd, ba.adms_stat_nm, " +
//                "EXTRACT(year FROM age(CURRENT_DATE, to_date(case a.rrno_2 when '3' then concat('20',a.rrno_1) when '4' then concat('20',a.rrno_1) else concat('19',a.rrno_1) end, 'yyyyMMdd'))) " +
//                "FROM info_pt a " +
//                "inner join bdas_req b on a.pt_id = b.pt_id " +
//                "left outer join bdas_adms ba on b.bdas_seq = ba.bdas_seq " +
//                "where b.bdas_seq in (select max(bdas_seq) as bdasSeq from bdas_req group by pt_id) " +
//                "order by b.updt_dttm desc"

//        return find(query).project(InfoPtSearchDto::class.java).list()
//        val criteriaBuilder = getEntityManager().criteriaBuilder
//        criteriaBuilder.createQuery()
        return getEntityManager().createQuery(query, InfoPtSearchDto::class.java).resultList
    }
}

@ApplicationScoped
class InfoCrewRepository : PanacheRepositoryBase<InfoCrew, InfoCrewId> {
    fun findInfoCrews(instId: String) = find("inst_id = '$instId'").list()
}

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String>

@ApplicationScoped
class InfoInstRepository : PanacheRepositoryBase<InfoInst, String> {

    fun findInstCodeList(dstrCd1: String, dstrCd2: String, instTypeCd: String) =
        find(
            "select i from InfoInst i where " +
                "('$dstrCd1' = '' or i.dstrCd1 = '$dstrCd1') and " +
                "('$dstrCd2' = '' or i.dstrCd2 = '$dstrCd2') and " +
                "('$instTypeCd' = '' or i.instTypeCd = '$instTypeCd')"
        ).list()

    fun findFireStatns(param: InstCdParameters) =
        find("inst_type_cd = 'ORGN0002' and dstr_cd_1 = ?1 and dstr_cd_2 = ?2", param.dstrCd1, param.dstrCd2).list()
}

@ApplicationScoped
class InfoCertRepository : PanacheRepositoryBase<InfoCert, String>
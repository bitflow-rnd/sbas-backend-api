package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.dtos.InfoHospDetailDto
import org.sbas.dtos.InfoPtSearchDto
import org.sbas.dtos.PagingListDto
import org.sbas.entities.info.*
import org.sbas.parameters.InstCdParameters
import org.sbas.parameters.SearchHospRequest
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.NotFoundException

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = ?1 AND rrno_1 = ?2 AND rrno_2 = ?3", ptNm, rrno1, rrno2).firstResult()

    //TODO 삭제 예정
    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr_1_cd = '$dstr1Cd' and dstr_2_cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(): List<InfoPtSearchDto> {
        //TODO
        val query = "select new org.sbas.dtos.InfoPtSearchDto(a.ptId, max(b.id.bdasSeq), a.ptNm, a.gndr, " +
                "a.dstr1Cd, split_part(a.bascAddr, ' ', 1), a.dstr2Cd, split_part(a.bascAddr, ' ', 2), " +
                "max(ba.hospId), '', a.mpno, a.natiCd, fn_get_bed_asgn_stat(a.ptId, max(b.id.bdasSeq)), '', a.updtDttm, " +
                "max(b.ptTypeCd), max(b.svrtTypeCd), max(b.undrDsesCd), " +
                "EXTRACT(year FROM age(CURRENT_DATE, to_date(case a.rrno2 when '3' then concat('20',a.rrno1) when '4' then concat('20',a.rrno1) else concat('19',a.rrno1) end, 'yyyyMMdd')))) " +
                "FROM InfoPt a " +
                "left join BdasReq b on a.ptId = b.id.ptId " +
                "left join BdasAdms ba on b.id.bdasSeq = ba.id.bdasSeq " +
                "group by a.ptId " +
                "order by a.updtDttm desc"
        return getEntityManager().createQuery(query, InfoPtSearchDto::class.java).resultList
    }
}

@ApplicationScoped
class InfoCrewRepository : PanacheRepositoryBase<InfoCrew, InfoCrewId> {
    fun findInfoCrews(instId: String) = find("inst_id = '$instId'").list()
}

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var userRepository: InfoUserRepository

    fun findInfoHospByHpId(hpIdList: MutableList<String>): InfoHosp? {
        return find("").firstResult()
    }

    fun findInfoHopByCondition(searchParam: SearchHospRequest?): PanacheQuery<InfoHosp> {
        val queryBuilder = StringBuilder("from InfoHosp where 1 = 1")

            searchParam?.dutyDivNam?.forEachIndexed { index, dutyDivName ->
                if (index == 0) {
                    queryBuilder.append(" and (")
                }else{
                    queryBuilder.append(" or ")
                }
                queryBuilder.append("dutyDivNam = '$dutyDivName'")

                if(index == searchParam.dutyDivNam.size -1){
                    queryBuilder.append(")")
                }
            }

        searchParam?.dstrCd1?.let { dstrCd1 ->
            queryBuilder.append(" and dutyAddr like fn_get_cd_nm('SIDO','$dstrCd1')||'%'")
        }
        searchParam?.dstrCd2?.let { dstrCd2 -> queryBuilder.append(" and dstr2Cd = '$dstrCd2'") }
        searchParam?.hospId?.let { hospId -> queryBuilder.append(" and (hospId = '$hospId' or dutyName like '%$hospId%')") }

        val query = queryBuilder.toString()

        return find(query)

    }

    fun findInfoHospDetail(hpId: String) : InfoHospDetailDto {
        val findHosp = find("from InfoHosp where hpId = '$hpId'").firstResult() ?: throw NotFoundException("Please check hpId")
        val count = userRepository.find("from InfoUser where instId = '$hpId'").count()

        return InfoHospDetailDto(findHosp, count)
    }

}

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

@ApplicationScoped
class InfoCntcRepository : PanacheRepositoryBase<InfoCntc, InfoCntcId> {

    fun getHistSeq(userId: String): Int? = find("from InfoCntc where id.userId = '$userId' order by id.histSeq desc").firstResult()?.id?.histSeq

    fun getMyUsers(userId: String) = find("from InfoCntc where id.userId = '$userId'").list()

}
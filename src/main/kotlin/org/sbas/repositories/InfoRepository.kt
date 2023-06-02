package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.dtos.info.InfoHospDetailDto
import org.sbas.dtos.info.InfoPtSearchDto
import org.sbas.entities.info.*
import org.sbas.parameters.InstCdParameters
import org.sbas.parameters.SearchHospRequest
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.ws.rs.NotFoundException

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    @Inject
    private lateinit var entityManager: EntityManager
    private lateinit var queryFactory: QueryFactory

    @PostConstruct
    fun initialize() {
        queryFactory = QueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager),
            subqueryCreator = SubqueryCreatorImpl()
        )
    }

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = '$ptNm' AND rrno_1 = '$rrno1' AND rrno_2 = '$rrno2'").firstResult()

    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr_1_cd = '$dstr1Cd' and dstr_2_cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(): List<InfoPtSearchDto> {
        //TODO
        val query = "select new org.sbas.dtos.info.InfoPtSearchDto(a.ptId, b.id.bdasSeq, a.ptNm, a.gndr, " +
                "a.dstr1Cd, fn_get_cd_nm('SIDO', a.dstr1Cd), a.dstr2Cd, fn_get_cd_nm('SIDO'||a.dstr1Cd, a.dstr2Cd), " +
                "ba.hospId, '', a.mpno, a.natiCd, fn_get_bed_asgn_stat(a.ptId, b.id.bdasSeq), '', a.updtDttm, " +
                "b.ptTypeCd, b.svrtTypeCd, b.undrDsesCd, fn_get_age(a.rrno1, a.rrno2)) " +
                "from InfoPt a " +
                "left join BdasReq b on a.ptId = b.id.ptId " +
                "left join BdasAdms ba on b.id.bdasSeq = ba.id.bdasSeq " +
                "where b.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or b.id.bdasSeq is null " +
                "order by a.updtDttm desc"
        return getEntityManager().createQuery(query, InfoPtSearchDto::class.java).resultList
    }

    fun updateAttcId(attcId: String): Int {
        return update("attcId = null where attcId = '${attcId}'")
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

    fun findByHospIdList(list: List<String>): List<InfoHosp> {
        val param = list.joinToString(
            separator = "','",
            prefix = "'",
            postfix = "'",
        )
        return list("hosp_id in (${param})")
    }

    fun findListByDstrCd1AndDstrCd2(dstrCd1: String, dstrCd2: String): List<InfoHosp> {
        return find("dstrCd1 = '$dstrCd1' and dstrCd2 = '$dstrCd2'").list()
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
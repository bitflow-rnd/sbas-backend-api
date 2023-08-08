package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.expression.function
import com.linecorp.kotlinjdsl.querydsl.expression.nullLiteral
import com.linecorp.kotlinjdsl.selectQuery
import com.linecorp.kotlinjdsl.subquery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.bdas.BdasReq
import org.sbas.entities.bdas.BdasReqId
import org.sbas.entities.info.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.criteria.AbstractQuery
import javax.ws.rs.NotFoundException

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var queryFactory: QueryFactory

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = '$ptNm' AND rrno_1 = '$rrno1' AND rrno_2 = '$rrno2'").firstResult()

    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr_1_cd = '$dstr1Cd' and dstr_2_cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(): MutableList<InfoPtSearchDto> {
//
//        val query = "select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId"
//        @Suppress("UNCHECKED_CAST")
//        val maxBdasSeqList = entityManager.createQuery(query).resultList as MutableList<Int>
//
//        val list = queryFactory.listQuery<InfoPtSearchDto> {
//            selectMulti(
//                col(InfoPt::ptId), col(BdasReqId::bdasSeq), col(InfoPt::ptNm), col(InfoPt::gndr),
//                col(InfoPt::dstr1Cd), function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoPt::dstr1Cd)),
//                col(InfoPt::dstr2Cd), function("fn_get_cd_nm", String::class.java, literal("SIDO"+col(InfoPt::dstr1Cd)), col(InfoPt::dstr2Cd)),
//                literal("hospId"), literal("hospNm"), col(InfoPt::mpno), col(InfoPt::natiCd),
//                col(BdasReq::bedStatCd), col(InfoPt::updtDttm)
//
//            )
//            from(entity(InfoPt::class))
//            join(entity(BdasReq::class), on { col(InfoPt::ptId).equal(col(BdasReqId::ptId))})
//            associate(entity(BdasReq::class), BdasReqId::class, on(BdasReq::id))
////            exists(subQuery)
//            whereOr(
//                col(BdasReqId::bdasSeq).`in`(maxBdasSeqList),
//                col(BdasReqId::bdasSeq).equal(nullLiteral()),
//            )
//        }
//        return list.toMutableList()
        //TODO 기관 이름추가
        val query = "select new org.sbas.dtos.info.InfoPtSearchDto(a.ptId, b.id.bdasSeq, a.ptNm, a.gndr, " +
                "a.dstr1Cd, fn_get_cd_nm('SIDO', a.dstr1Cd), a.dstr2Cd, fn_get_cd_nm('SIDO'||a.dstr1Cd, a.dstr2Cd), " +
                "ba.hospId, '', a.mpno, a.natiCd, b.bedStatCd, a.updtDttm, " +
                "b.ptTypeCd, b.svrtTypeCd, b.undrDsesCd, fn_get_age(a.rrno1, a.rrno2)) " +
                "from InfoPt a " +
                "left join BdasReq b on a.ptId = b.id.ptId " +
                "left join BdasAdms ba on b.id.bdasSeq = ba.id.bdasSeq " +
                "where b.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or b.id.bdasSeq is null " +
                "order by a.updtDttm desc"
        return entityManager.createQuery(query, InfoPtSearchDto::class.java).resultList
    }

    fun updateAttcId(attcId: String): Int {
        return update("attcId = null where attcId = '${attcId}'")
    }

    fun getAge(rrno1: String?, rrno2: String?): Int {
        val query = "select fn_get_age('${rrno1}', '${rrno2}') as test"
        return entityManager.createNativeQuery(query).singleResult as Int
    }

    fun findBdasHisInfo(ptId: String): MutableList<BdasHisInfo> {
        val query = "select new org.sbas.dtos.info.BdasHisInfo(be.ptId, be.bdasSeq, " +
                "be.diagNm, ih.dutyName, '', ba.updtDttm, br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd) " +
                "from BdasEsvy be " +
                "join BdasReq br on be.bdasSeq = br.id.bdasSeq " +
                "left join BdasAdms ba on be.id.bdasSeq = ba.id.bdasSeq " +
                "join InfoHosp ih on ba.hospId = ih.hospId " +
                "where be.ptId = '${ptId}' " +
                "order by ba.id.bdasSeq desc"

        return entityManager.createQuery(query, BdasHisInfo::class.java).resultList.toMutableList()
    }
}

@ApplicationScoped
class InfoCrewRepository : PanacheRepositoryBase<InfoCrew, InfoCrewId> {

    @Inject
    private lateinit var queryFactory: QueryFactory

    fun findInfoCrews(param: InfoCrewSearchParam): MutableList<InfoCrewDto> {
        val infoCrewList = queryFactory.listQuery<InfoCrewDto> {
            selectMulti(
                col(InfoCrewId::instId), col(InfoCrewId::crewId), col(InfoCrew::crewNm),
                col(InfoCrew::telno), col(InfoCrew::rmk), col(InfoCrew::pstn),
            )
            from(entity(InfoCrew::class))
            associate(entity(InfoCrew::class), InfoCrewId::class, on(InfoCrew::id))
            whereAnd(
                col(InfoCrewId::instId).equal(param.instId),
                param.crewId?.run { col(InfoCrewId::crewId).like("%$this%") },
                param.crewNm?.run { col(InfoCrew::crewNm).like("%$this%") },
                param.telno?.run { col(InfoCrew::telno).like("%$this%") },
            )
        }

        return infoCrewList.toMutableList()
    }

    fun findInfoCrew(instId: String, crewId: String): InfoCrew? {
        return find("inst_id = '$instId' and crew_id = '$crewId'").firstResult()
    }

    fun countInfoCrewsGroupByInstId(): MutableList<CrewCountList> {
        val query = "select new org.sbas.dtos.info.CrewCountList(count(ic.id.crewId), ic.id.instId) " +
                "from InfoCrew ic " +
                "group by ic.id.instId "

        return getEntityManager().createQuery(query, CrewCountList::class.java).resultList
    }

    fun findLatestCrewId(instId: String): String? {
        return find("inst_id = '$instId'", Sort.by("crew_id", Sort.Direction.Descending))
            .firstResult()?.id?.crewId
    }
}

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

    @Inject
    private lateinit var log:Logger

    @Inject
    private lateinit var queryFactory: QueryFactory

    @Inject
    private lateinit var userRepository: InfoUserRepository

    fun findInfoHospByHospId(hospId: String): InfoHosp? {
        return find("hosp_id = '$hospId'").firstResult()
    }

    fun findInfoHosps(param: InfoHospSearchParam): MutableList<InfoHospListDto> {
        val infoHosps = queryFactory.listQuery<InfoHospListDto> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dutyName), col(InfoHosp::dutyDivNam),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2), col(InfoHosp::dutyTel1), col(InfoHosp::dutyTel1), col(InfoHosp::updtDttm),
            )
            from(entity(InfoHosp::class))
            limit(20)
            param.page?.run { offset(this.minus(1).times(20)) }
            whereAndOrder(param)
        }

        return infoHosps.toMutableList()
    }

    fun countInfoHosps(param: InfoHospSearchParam): Int {
        val count = queryFactory.listQuery {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dutyName), col(InfoHosp::dutyDivNam),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2), col(InfoHosp::dutyTel1), col(InfoHosp::dutyTel1),col(InfoHosp::updtDttm),
            )
            from(entity(InfoHosp::class))
            whereAndOrder(param)
        }
        return count.size
    }

    private fun CriteriaQueryDsl<InfoHospListDto>.whereAndOrder(param: InfoHospSearchParam) {
        whereAnd(
            param.hospId?.run { col(InfoHosp::hospId).like("%$this%") },
            param.dutyName?.run { col(InfoHosp::dutyName).like("%$this%") },
            param.dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
            param.dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
            param.dutyDivNam?.run { col(InfoHosp::dutyDivNam).`in`(this) },
        )
        orderBy(
            ExpressionOrderSpec(col(InfoHosp::hospId), ascending = false),
        )
    }

    fun findInfoHospDetail(hpId: String) : InfoHospDetailDto {
        val findHosp = find("from InfoHosp where hpId = '$hpId'").firstResult() ?: throw NotFoundException("Please check hpId")
        val count = userRepository.find("from InfoUser where instId = '$hpId'").count()

        return InfoHospDetailDto(findHosp, count)
    }

    fun findByHospIdList(hospList: List<String>): MutableList<InfoHospWithUser> {
        val infoHospList = queryFactory.listQuery<InfoHospWithUser> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::dutyName),
                col(InfoUser::instId), col(InfoUser::instNm), col(InfoUser::id)
            )
            from(entity(InfoHosp::class))
            join(entity(InfoUser::class), on { col(InfoHosp::hospId).equal(col(InfoUser::instId)) })
            whereAnd(
                col(InfoUser::instId).`in`(hospList),
                col(InfoUser::jobCd).equal("PMGR0003"),
            )
        }

        return infoHospList.toMutableList()
    }
    fun findListByDstrCd1AndDstrCd2(dstrCd1: String, dstrCd2: String?): MutableList<InfoHosp> {
//        val list = queryFactory.listQuery<InfoHosp> {
//            select(
//                entity(InfoHosp::class)
//            )
//            from(entity(InfoHosp::class))
//            join(entity(InfoUser::class), on { col(InfoHosp::hospId).equal(col(InfoUser::instId)) })
//            whereAnd(
//                col(InfoHosp::dstrCd1).equal(dstrCd1),
//                dstrCd2?.run { col(InfoHosp::dstrCd1).equal(this) },
//            )
//        }
//        return list

        val query = "select ih.* from info_hosp ih join info_user iu on ih.hosp_id = iu.inst_id"

        val where = if (dstrCd2 != null) {
            " where ih.dstr_cd1 = '$dstrCd1' and ih.dstr_cd2 = '$dstrCd2' and (iu.job_cd = 'PMGR0003' OR iu.job_cd like '병상배정%') "
        } else {
            " where ih.dstr_cd1 = '$dstrCd1' and (iu.job_cd = 'PMGR0003' OR iu.job_cd like '병상배정%') "
        }

        @Suppress("UNCHECKED_CAST")
        return getEntityManager().createNativeQuery(query + where, InfoHosp::class.java).resultList.toMutableList() as MutableList<InfoHosp>
    }
}

@ApplicationScoped
class InfoInstRepository : PanacheRepositoryBase<InfoInst, String> {

    @Inject
    private lateinit var queryFactory: QueryFactory

    fun findInstCodeList(dstrCd1: String, dstrCd2: String, instTypeCd: String) =
        find(
            "select i from InfoInst i where " +
                "('$dstrCd1' = '' or i.dstrCd1 = '$dstrCd1') and " +
                "('$dstrCd2' = '' or i.dstrCd2 = '$dstrCd2') and " +
                "('$instTypeCd' = '' or i.instTypeCd = '$instTypeCd')"
        ).list()

    fun findFireStatns(param: FireStatnSearchParam): MutableList<FireStatnListDto> {

        val fireStatnList: List<FireStatnListDto> = queryFactory.listQuery {
            selectMulti(
                col(InfoInst::id), col(InfoInst::instNm),
                function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoInst::dstrCd1)),
                function("fn_get_dstr_cd2_nm", String::class.java, col(InfoInst::dstrCd1), col(InfoInst::dstrCd2)),
                col(InfoInst::chrgTelno),
            )
            from(entity(InfoInst::class))
            whereAnd(
                col(InfoInst::instTypeCd).equal("ORGN0002"),
                param.instId?.run { col(InfoInst::id).like("%$this%") },
                param.instNm?.run { col(InfoInst::instNm).like("%$this%") },
                param.dstrCd1?.run { col(InfoInst::dstrCd1).equal(this) },
                param.dstrCd2?.run { col(InfoInst::dstrCd2).equal(this) },
                param.chrgTelno?.run { col(InfoInst::chrgTelno).like("%$this%") },
            )
            orderBy(
                ExpressionOrderSpec(col(InfoInst::id), ascending = false),
                ExpressionOrderSpec(col(InfoInst::rgstDttm), ascending = false),
                ExpressionOrderSpec(col(InfoInst::instNm), ascending = false),
            )
        }

        return fireStatnList.toMutableList()
    }

    fun findLatestFireStatInstId(): String? {
        return find("inst_type_cd = 'ORGN0002'", Sort.by("inst_id", Sort.Direction.Descending)).firstResult()?.id
    }

    fun findFireStatn(instId: String): InfoInst? {
        return find("inst_type_cd = 'ORGN0002' and inst_id = '$instId'").firstResult()
    }
}

@ApplicationScoped
class InfoCertRepository : PanacheRepositoryBase<InfoCert, String>

@ApplicationScoped
class InfoCntcRepository : PanacheRepositoryBase<InfoCntc, InfoCntcId> {

    fun getHistSeq(userId: String): Int? = find("from InfoCntc where id.userId = '$userId' order by id.histSeq desc").firstResult()?.id?.histSeq

    fun getMyUsers(userId: String) = find("from InfoCntc where id.userId = '$userId'").list()

}
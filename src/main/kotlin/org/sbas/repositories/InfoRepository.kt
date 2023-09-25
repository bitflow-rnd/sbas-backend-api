package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.subquery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.info.*
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.ws.rs.NotFoundException

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var queryFactory: QueryFactory

    @Inject
    private lateinit var log: Logger

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("pt_nm = '$ptNm' AND rrno_1 = '$rrno1' AND rrno_2 = '$rrno2'").firstResult()

    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr_1_cd = '$dstr1Cd' and dstr_2_cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(param: InfoPtSearchParam): List<InfoPtSearchDto> {
        val (cond, offset) = conditionAndOffset(param)

        val query = "select new org.sbas.dtos.info.InfoPtSearchDto(pt.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, pt.rrno1, " +
                "pt.dstr1Cd, fn_get_cd_nm('SIDO', pt.dstr1Cd), pt.dstr2Cd, fn_get_cd_nm('SIDO'||pt.dstr1Cd, pt.dstr2Cd), " +
                "bap.hospId, ih.dutyName, pt.mpno, pt.natiCd, pt.natiNm, br.bedStatCd, pt.updtDttm, pt.rgstDttm, " +
                "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, fn_get_age(pt.rrno1, pt.rrno2)) " +
                "from InfoPt pt " +
                "left join BdasReq br on pt.ptId = br.id.ptId " +
                "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
                "left join InfoHosp ih on bap.hospId = ih.hospId " +
                "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
                "$cond " +
                "order by pt.rgstDttm desc "

        return entityManager.createQuery(query, InfoPtSearchDto::class.java).setMaxResults(15).setFirstResult(offset).resultList
    }

    fun countInfoPtList(param: InfoPtSearchParam): Long {
        val (cond, _) = conditionAndOffset(param)

        val query = "select count(pt.ptId) " +
                "from InfoPt pt " +
                "left join BdasReq br on pt.ptId = br.id.ptId " +
                "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
                "left join InfoHosp ih on bap.hospId = ih.hospId " +
                "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
                "$cond "

        return entityManager.createQuery(query).singleResult as Long
    }

    fun findHospNmList(param: InfoPtSearchParam): List<*> {
        val cond = condition(param)

        val query = "select distinct ih.dutyName " +
                "from InfoPt pt " +
                "left join BdasReq br on pt.ptId = br.id.ptId " +
                "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
                "left join InfoHosp ih on bap.hospId = ih.hospId " +
                "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
                "$cond "

        return entityManager.createQuery(query).resultList
    }

    fun updateAttcId(attcId: String): Int {
        return update("attcId = null where attcId = '${attcId}'")
    }

    fun getAge(rrno1: String?, rrno2: String?): Int {
        val query = "select fn_get_age('${rrno1}', '${rrno2}') as test"
        return entityManager.createNativeQuery(query).singleResult as Int
    }

    fun findBdasHisInfo(ptId: String): MutableList<BdasHisInfo> {
        val query = "select new org.sbas.dtos.info.BdasHisInfo(be.ptId, be.bdasSeq, br.bedStatCd, " +
                "be.diagNm, ih.dutyName, ba.updtDttm, br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd) " +
                "from BdasEsvy be " +
                "join BdasReq br on be.bdasSeq = br.id.bdasSeq " +
                "left join BdasAprv ba on (be.bdasSeq = ba.id.bdasSeq and ba.aprvYn = 'Y') " +
                "left join InfoHosp ih on ba.hospId = ih.hospId " +
                "where be.ptId = '${ptId}' " +
                "order by ba.id.bdasSeq desc"

        return entityManager.createQuery(query, BdasHisInfo::class.java).resultList.toMutableList()
    }

    private fun condition(param: InfoPtSearchParam): String {
        var cond2 = " (and pt.ptNm like '%${param.ptNm ?: ""}%' " +
                "or pt.rrno1 like '%${param.rrno1 ?: ""}%' " +
                "or pt.mpno like '%${param.mpno ?: ""}%') "

        var cond = param.ptNm?.run { " and (pt.ptNm like '%$this%' " } ?: "and (1=1"
        cond += param.rrno1?.run { " or pt.rrno1 like '%$this%' " } ?: ""
        cond += param.mpno?.run { " or pt.mpno like '%$this%') " } ?: ") "
        cond += param.ptId?.run { " and pt.ptId like '%$this%' " } ?: ""

        cond += param.sever?.run { " and pt.ptId in (select sa.id.ptId from SvrtAnly sa) " } ?: ""
        cond += param.gndr?.run { " and pt.gndr like '%$this%' " } ?: ""
        cond += param.natiCd?.run { " and pt.natiCd like '%$this%' " } ?: ""
        cond += param.dstr1Cd?.run { " and pt.dstr1Cd like '%$this%' " } ?: ""
        cond += param.dstr2Cd?.run { " and pt.dstr2Cd like '%$this%' " } ?: ""
        cond += param.hospNm?.run { " and ih.dutyName like '%$this%' " } ?: ""

        cond += param.bedStatCd?.run {
            if (this.contains("BAST0001")) {
                " and br.bedStatCd in ('${this.split(',').joinToString("', '")}') or br.bedStatCd is null "
            } else {
                " and br.bedStatCd in ('${this.split(',').joinToString("', '")}') "
            }
        } ?: ""

        cond += param.period?.run {
            " and pt.${param.dateType} > '${
                Instant.now().minusSeconds(60 * 60 * 24 * this)
            }' "
        } ?: ""

        return cond
    }

    private fun conditionAndOffset(param: InfoPtSearchParam): Pair<String, Int> {
        val cond = condition(param)
        val offset = param.page?.run { this.minus(1).times(15) } ?: 0

        return Pair(cond, offset)
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
                param.crewId?.run { col(InfoCrewId::crewId).equal(this) },
                param.crewNm?.run { col(InfoCrew::crewNm).like("%$this%") },
                param.telno?.run { col(InfoCrew::telno).like("%$this%") },
            )
        }

        return infoCrewList.toMutableList()
    }

    fun findInfoCrew(instId: String, crewId: String): InfoCrew? {
        return find("inst_id = '$instId' and crew_id = '$crewId'").firstResult()
    }

    fun findLatestCrewId(instId: String): Int? {
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

    fun findInfoHospByHpId(hpId: String): InfoHospId {
        val infoHospId = queryFactory.listQuery<InfoHospId> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dstrCd1),
                function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoHosp::dstrCd1)),
            )
            from(entity(InfoHosp::class))
            where(
                col(InfoHosp::hpId).equal(hpId)
            )
        }

        return infoHospId[0]
    }

    fun findInfoHosps(param: InfoHospSearchParam): MutableList<InfoHospListDto> {
        val infoHosps = queryFactory.listQuery<InfoHospListDto> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dutyName), col(InfoHosp::dutyDivNam),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2), col(InfoHosp::dutyTel1), col(InfoHosp::dutyTel1), col(InfoHosp::updtDttm),
                col(InfoBed::gnbdIcu), col(InfoBed::npidIcu), col(InfoBed::gnbdSvrt),
                col(InfoBed::gnbdSmsv), col(InfoBed::gnbdModr),
                col(InfoBed::ventilator), col(InfoBed::ventilatorPreemie), col(InfoBed::incubator), col(InfoBed::ecmo),
                col(InfoBed::highPressureOxygen), col(InfoBed::ct), col(InfoBed::mri), col(InfoBed::highPressureOxygen),
                col(InfoBed::bodyTemperatureControl),
                col(InfoBed::emrgncyNrmlBed), col(InfoBed::ngtvIsltnChild), col(InfoBed::nrmlIsltnChild),
                col(InfoBed::nrmlChildBed), col(InfoBed::emrgncyNrmlIsltnBed),
            )
            from(entity(InfoHosp::class))
            join(entity(InfoBed::class), on { col(InfoHosp::hospId).equal(col(InfoBed::hospId)) })
            limit(15)
            param.page?.run { offset(this.minus(1).times(15)) }
            whereAnd(param)
            orderBy(
                ExpressionOrderSpec(col(InfoHosp::hospId), ascending = false),
            )
        }

        return infoHosps.toMutableList()
    }

    fun countInfoHosps(param: InfoHospSearchParam): Int {
        val count = queryFactory.listQuery<Long> {
            selectMulti(count(entity(InfoHosp::class)))
            from(entity(InfoHosp::class))
            join(entity(InfoBed::class), on { col(InfoHosp::hospId).equal(col(InfoBed::hospId)) })
            whereAnd(param)
        }
        return count[0].toInt()
    }

    // TODO
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

    fun findAvalHospListByDstrCd1(dstrCd1: String): MutableList<AvalHospDto> {
        val list = queryFactory.listQuery<AvalHospDto> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::dutyName), col(InfoHosp::wgs84Lon), col(InfoHosp::wgs84Lat),
                col(InfoHosp::dutyAddr), col(InfoBed::gnbdIcu), col(InfoBed::npidIcu), col(InfoBed::gnbdSvrt),
                col(InfoBed::gnbdSmsv), col(InfoBed::gnbdModr),
                col(InfoBed::ventilator), col(InfoBed::ventilatorPreemie), col(InfoBed::incubator), col(InfoBed::ecmo),
                col(InfoBed::highPressureOxygen), col(InfoBed::ct), col(InfoBed::mri), col(InfoBed::highPressureOxygen),
                col(InfoBed::bodyTemperatureControl),
            )
            from(entity(InfoHosp::class))
            join(entity(InfoBed::class), on { col(InfoHosp::hospId).equal(col(InfoBed::hospId)) })
            whereAnd(
                col(InfoHosp::dstrCd1).equal(dstrCd1),
            )
        }

        return list.toMutableList()

//        val query = "select ih.* from info_hosp ih join info_user iu on ih.hosp_id = iu.inst_id "
//
//        val where = if (!dstrCd2.isNullOrBlank()) {
//            " where ih.dstr_cd1 = '$dstrCd1' and ih.dstr_cd2 = '$dstrCd2' and (iu.job_cd = 'PMGR0003' OR iu.job_cd like '병상배정%') "
//        } else {
//            " where ih.dstr_cd1 = '$dstrCd1' and (iu.job_cd = 'PMGR0003' OR iu.job_cd like '병상배정%') "
//        }

//        val query = "select ih.* from info_hosp ih join info_bed ib on ih.hosp_id = ib.hosp_id "
//
//        val where = if (!dstrCd2.isNullOrBlank()) {
//            " where ih.dstr_cd1 = '$dstrCd1' and ih.dstr_cd2 = '$dstrCd2' "
//        } else {
//            " where ih.dstr_cd1 = '$dstrCd1' "
//        }

//        @Suppress("UNCHECKED_CAST")
//        return getEntityManager().createNativeQuery(query + where, InfoHosp::class.java).resultList.toMutableList() as MutableList<InfoHosp>
    }

    fun findPubHealthCenter(dstrCd1: String?, dstrCd2: String?): List<InfoInstResponse> {
        val healthCenterList = queryFactory.listQuery<InfoInstResponse> {
            selectMulti(
                col(InfoHosp::hospId), literal("ORGN0003"), col(InfoHosp::dutyName),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2),
            )
            from(entity(InfoHosp::class))
            whereAnd(
                col(InfoHosp::dutyDivNam).equal("보건소"),
                dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
                dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
            )
        }

        return healthCenterList
    }

    private fun CriteriaQueryDsl<*>.whereAnd(param: InfoHospSearchParam) {
        whereAnd(
            param.hospId?.run { col(InfoHosp::hospId).like("%$this%") },
            param.dutyName?.run { col(InfoHosp::dutyName).like("%$this%") },
            param.dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
            param.dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
            param.dutyDivNam?.run { col(InfoHosp::dutyDivNam).`in`(this) },
        )
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


    fun findInfoInst(dstrCd1: String?, dstrCd2: String?, instTypeCd: String?): List<InfoInstResponse> {
        val list = queryFactory.listQuery<InfoInstResponse> {
            selectMulti(
                col(InfoInst::id), col(InfoInst::instTypeCd), col(InfoInst::instNm),
                col(InfoInst::dstrCd1), col(InfoInst::dstrCd2),
            )
            from(entity(InfoInst::class))
            whereAnd(
                instTypeCd?.run { col(InfoInst::instTypeCd).equal(this) },
                dstrCd1?.run { col(InfoInst::dstrCd1).equal(this) },
                dstrCd2?.run { col(InfoInst::dstrCd2).equal(this) },
            )
        }

        return list
    }

    fun findFireStatns(param: FireStatnSearchParam): MutableList<FireStatnListDto> {
        val fireStatnList: List<FireStatnListDto> = queryFactory.listQuery {
            val crewCount = queryFactory.subquery {
                select(
                    count(col(InfoCrewId::crewId))
                )
                from(entity(InfoCrew::class))
                associate(entity(InfoCrew::class), InfoCrewId::class, on(InfoCrew::id))
                groupBy(col(InfoCrewId::instId))
                where(col(InfoCrewId::instId).equal(col(InfoInst::id)))
            }
            selectMulti(
                col(InfoInst::id), col(InfoInst::instNm),
                function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoInst::dstrCd1)),
                function("fn_get_dstr_cd2_nm", String::class.java, col(InfoInst::dstrCd1), col(InfoInst::dstrCd2)),
                col(InfoInst::chrgTelno), crewCount
            )
            from(entity(InfoInst::class))
            fireStatnsWhereAnd(param)
            limit(15)
            param.page?.run { offset(this.minus(1).times(15)) }
            orderBy(
                ExpressionOrderSpec(col(InfoInst::id), ascending = false),
                ExpressionOrderSpec(col(InfoInst::rgstDttm), ascending = false),
                ExpressionOrderSpec(col(InfoInst::instNm), ascending = false),
            )
        }

        return fireStatnList.toMutableList()
    }

    fun countFireStatns(param: FireStatnSearchParam): Int {
        val count = queryFactory.listQuery<Long> {
            selectMulti((count(entity(InfoInst::class))))
            from(entity(InfoInst::class))
            fireStatnsWhereAnd(param)
        }

        return count[0].toInt()
    }

    fun findLatestFireStatInstId(): String? {
        return find("inst_type_cd = 'ORGN0002'", Sort.by("inst_id", Sort.Direction.Descending)).firstResult()?.id
    }

    fun findFireStatnDtoByInstId(instId: String): FireStatnDto? {
        val query = "select new org.sbas.dtos.info.FireStatnDto(ii.id, ii.instNm, " +
                "ii.chrgId, ii.chrgNm, ii.dstrCd1, fn_get_cd_nm('SIDO', ii.dstrCd1), " +
                "ii.dstrCd2, fn_get_cd_nm('SIDO'||ii.dstrCd1, ii.dstrCd2), " +
                "ii.chrgTelno, ii.rmk, ii.detlAddr, ii.lat, ii.lon, ii.vecno ) " +
                "from InfoInst ii " +
                "where ii.instTypeCd = 'ORGN0002' and ii.id = '$instId' "

        return getEntityManager().createQuery(query, FireStatnDto::class.java).singleResult
    }

    fun findFireStatn(instId: String): InfoInst? {
        return find("inst_type_cd = 'ORGN0002' and inst_id = '$instId'").firstResult()
    }

    private fun CriteriaQueryDsl<*>.fireStatnsWhereAnd(param: FireStatnSearchParam) {
        whereAnd(
            col(InfoInst::instTypeCd).equal("ORGN0002"),
            param.instId?.run { col(InfoInst::id).like("%$this%") },
            param.instNm?.run { col(InfoInst::instNm).like("%$this%") },
            param.dstrCd1?.run { col(InfoInst::dstrCd1).equal(this) },
            param.dstrCd2?.run { col(InfoInst::dstrCd2).equal(this) },
            param.chrgTelno?.run { col(InfoInst::chrgTelno).like("%$this%") },
        )
    }
}

@ApplicationScoped
class InfoCntcRepository : PanacheRepositoryBase<InfoCntc, InfoCntcId> {

    fun getHistSeq(userId: String): Int? = find("from InfoCntc where id.userId = '$userId' order by id.histSeq desc").firstResult()?.id?.histSeq

    fun getMyUsers(userId: String) = find("from InfoCntc where id.userId = '$userId'").list()

}

@ApplicationScoped
class InfoBedRepository : PanacheRepositoryBase<InfoBed, String> {

    fun findByHpid(hpid: String): InfoBed? {
        return find("hpid = '$hpid'").firstResult()
    }
}
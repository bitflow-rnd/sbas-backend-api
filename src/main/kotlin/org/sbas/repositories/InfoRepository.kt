package org.sbas.repositories

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.ws.rs.NotFoundException
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.info.*
import java.time.Instant

@ApplicationScoped
class InfoPtRepository : PanacheRepositoryBase<InfoPt, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var log: Logger

    fun findByPtNmAndRrno(ptNm: String, rrno1: String, rrno2: String): InfoPt? =
        find("ptNm = '$ptNm' AND rrno1 = '$rrno1' AND rrno2 = '$rrno2'").firstResult()

    fun findByDstrCd(dstr1Cd: String, dstr2Cd: String): List<InfoPt> {
        return find("dstr1Cd = '$dstr1Cd' and dstr2Cd = '$dstr2Cd'").list()
    }

    fun findInfoPtList(param: InfoPtSearchParam): List<InfoPtSearchDto> {
        val (cond, offset) = conditionAndOffset(param)

        val query = "select new org.sbas.dtos.info.InfoPtSearchDto(pt.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, pt.rrno1, " +
                "pt.dstr1Cd, fn_get_cd_nm('SIDO', pt.dstr1Cd), pt.dstr2Cd, fn_get_cd_nm('SIDO'||pt.dstr1Cd, pt.dstr2Cd), " +
                "bap.hospId, ih.dutyName, pt.mpno, pt.natiCd, pt.natiNm, br.bedStatCd, pt.rgstDttm, pt.updtDttm, " +
                "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, fn_get_age(pt.rrno1, pt.rrno2), pt.ptId in (select sa.id.ptId from SvrtAnly sa)) " +
                "from InfoPt pt " +
                "left join BdasReq br on pt.ptId = br.id.ptId " +
                "left join BdasAprv bap on (br.id.bdasSeq = bap.id.bdasSeq and bap.aprvYn = 'Y') " +
                "left join InfoHosp ih on bap.hospId = ih.hospId " +
                "where (br.id.bdasSeq in ((select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId)) or br.id.bdasSeq is null) " +
                "$cond " +
                "order by pt.updtDttm desc "

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
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    fun findInfoCrews(param: InfoCrewSearchParam): MutableList<InfoCrewDto> {
        val query = jpql {
            selectNew<InfoCrewDto>(
                path(InfoCrew::id)(InfoCrewId::instId), path(InfoCrew::id)(InfoCrewId::crewId),
                path(InfoCrew::crewNm), path(InfoCrew::telno), path(InfoCrew::rmk), path(InfoCrew::pstn),
            ).from(
                entity(InfoCrew::class)
            ).whereAnd(
                path(InfoCrew::id)(InfoCrewId::instId).eq(param.instId),
                param.crewId?.run { path(InfoCrew::id)(InfoCrewId::crewId).eq(this) },
                param.crewNm?.run { path(InfoCrew::crewNm).like("%$this%") },
                param.telno?.run { path(InfoCrew::telno).like("%$this%") },
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findInfoCrew(instId: String, crewId: Int): InfoCrew? {
        return find("id.instId = '$instId' and id.crewId = $crewId").firstResult()
    }

    fun findLatestCrewId(instId: String): Int {
        return find("id.instId = '$instId'", Sort.by("id.crewId", Sort.Direction.Descending))
            .firstResult()?.id?.crewId ?: 0
    }
}

@ApplicationScoped
class InfoInstRepository : PanacheRepositoryBase<InfoInst, String> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    fun findInfoInst(dstr1Cd: String?, dstr2Cd: String?, instTypeCd: String?): List<InfoInstResponse> {
        val query = jpql {
            selectNew<InfoInstResponse>(
                path(InfoInst::id), path(InfoInst::instTypeCd), path(InfoInst::instNm),
                path(InfoInst::dstr1Cd), path(InfoInst::dstr2Cd),
            ).from(
                entity(InfoInst::class),
            ).whereAnd(
                instTypeCd?.run { path(InfoInst::instTypeCd).equal(this) },
                dstr1Cd?.run { path(InfoInst::dstr1Cd).equal(this) },
                dstr2Cd?.run { path(InfoInst::dstr2Cd).equal(this) },
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findFireStatns(param: FireStatnSearchParam): MutableList<FireStatnListDto> {
        val query = jpql { 
            val crewCount = select(
                count(path(InfoCrew::id)(InfoCrewId::crewId))
            ).from(
                entity(InfoCrew::class)
            ).where(
                path(InfoCrew::id)(InfoCrewId::instId).eq(path(InfoInst::id))
            ).asSubquery()
            
            selectNew<FireStatnListDto>(
                path(InfoInst::id), path(InfoInst::instNm),
                function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoInst::dstr1Cd)),
                function(String::class, "fn_get_dstr_cd2_nm", path(InfoInst::dstr1Cd), path(InfoInst::dstr2Cd)),
                path(InfoInst::chrgTelno), crewCount, path(InfoInst::lat), path(InfoInst::lon)
            ).from(
                entity(InfoInst::class)
            ).whereAnd(
                path(InfoInst::instTypeCd).eq("ORGN0002"),
                param.instId?.run { path(InfoInst::id).like("%$this%") },
                param.instNm?.run { path(InfoInst::instNm).like("%$this%") },
                param.dstr1Cd?.run { path(InfoInst::dstr1Cd).eq(this) },
                param.dstr2Cd?.run { path(InfoInst::dstr2Cd).eq(this) },
                param.chrgTelno?.run { path(InfoInst::chrgTelno).like("%$this%") },
            ).orderBy(
                path(InfoInst::id).desc(),
                path(InfoInst::rgstDttm).desc(),
                path(InfoInst::instNm).desc(),
            )
        }

        val offset = param.page?.run { this.minus(1).times(15) } ?: 0
        val createQuery = entityManager.createQuery(query, context).setMaxResults(15).setFirstResult(offset)

        return createQuery.resultList
    }

    fun countFireStatns(param: FireStatnSearchParam): Int {
        val query = jpql {
            select(
                count(entity(InfoInst::class))
            ).from(
                entity(InfoInst::class)
            ).whereAnd(
                path(InfoInst::instTypeCd).eq("ORGN0002"),
                param.instId?.run { path(InfoInst::id).like("%$this%") },
                param.instNm?.run { path(InfoInst::instNm).like("%$this%") },
                param.dstr1Cd?.run { path(InfoInst::dstr1Cd).eq(this) },
                param.dstr2Cd?.run { path(InfoInst::dstr2Cd).eq(this) },
                param.chrgTelno?.run { path(InfoInst::chrgTelno).like("%$this%") },
            )
        }

        return entityManager.createQuery(query, context).singleResult.toInt()
    }

    fun findLatestFireStatInstId(): String? {
        return find("instTypeCd = 'ORGN0002'", Sort.by("id", Sort.Direction.Descending)).firstResult()?.id
    }

    fun findFireStatnDtoByInstId(instId: String): FireStatnDto? {
        val query = "select new org.sbas.dtos.info.FireStatnDto(ii.id, ii.instNm, " +
                "ii.chrgId, ii.chrgNm, ii.dstr1Cd, fn_get_cd_nm('SIDO', ii.dstr1Cd), " +
                "ii.dstr2Cd, fn_get_cd_nm('SIDO'||ii.dstr1Cd, ii.dstr2Cd), " +
                "ii.chrgTelno, ii.rmk, ii.detlAddr, ii.lat, ii.lon, ii.vecno ) " +
                "from InfoInst ii " +
                "where ii.instTypeCd = 'ORGN0002' and ii.id = '$instId' "

        return getEntityManager().createQuery(query, FireStatnDto::class.java).singleResult
    }

    fun findFireStatn(instId: String): InfoInst {
        return find("instTypeCd = 'ORGN0002' and id = '$instId'").firstResult() ?: throw NotFoundException("fire station not found")
    }

}

@ApplicationScoped
class InfoCntcRepository : PanacheRepositoryBase<InfoCntc, InfoCntcId> {

    fun getHistSeq(userId: String): Int? = find("from InfoCntc where id.userId = '$userId' order by id.histSeq desc").firstResult()?.id?.histSeq

    fun getMyUsers(userId: String) = find("from InfoCntc where id.userId = '$userId'").list()

    fun findInfoCntcByUserIdAndMbrId(userId: String, mbrId: String) = find("from InfoCntc where id.userId = '$userId' and id.mbrId = '$mbrId'").firstResult()

}

@ApplicationScoped
class InfoBedRepository : PanacheRepositoryBase<InfoBed, String> {

    fun findByHospId(hospId: String): InfoBed? {
        return find("hospId = '$hospId'").firstResult()
    }

    fun findByHpId(hpId: String): InfoBed? {
        return find("hpId = '$hpId'").firstResult()
    }
}
package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import jakarta.ws.rs.NotFoundException
import org.sbas.constants.enums.AdmsStatCd
import org.sbas.constants.enums.TimeLineStatCd
import org.sbas.dtos.bdas.*
import org.sbas.entities.bdas.*
import org.sbas.responses.patient.DestinationInfo
import java.time.Instant

@ApplicationScoped
class BdasEsvyRepository : PanacheRepositoryBase<BdasEsvy, Int> {
    fun findByPtIdWithLatestBdasSeq(ptId: String): BdasEsvy {
        return find("ptId = '${ptId}'", Sort.by("bdasSeq", Sort.Direction.Descending)).firstResult()
            ?: throw NotFoundException("해당 환자의 질병정보를 찾을 수 없습니다.")
    }
}

@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId> {

    @Inject
    private lateinit var entityManager: EntityManager

    fun findByPtIdWithLatestBdasSeq(ptId: String) = find("from BdasReq where id.ptId='$ptId' order by id.bdasSeq desc").firstResult()

    fun findByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): BdasReq {
        return find(
            "id.ptId = '${ptId}' and id.bdasSeq = $bdasSeq",
            Sort.by("id.bdasSeq", Sort.Direction.Descending)
        ).firstResult() ?: throw NotFoundException("$ptId $bdasSeq 병상요청 정보가 없습니다.")
    }

    fun queryForBdasList(cond: String?, offset: Int?): TypedQuery<BdasListDto> {
        val query = "select new org.sbas.dtos.bdas.BdasListDto(br.id.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, fn_get_age(pt.rrno1, pt.rrno2), " +
                "pt.rrno1, pt.mpno, pt.bascAddr, br.updtDttm, be.diagNm, br.bedStatCd, fn_find_chrg_inst(br.bedStatCd, br.id.ptId, br.id.bdasSeq), br.inhpAsgnYn, " +
                "br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd, br.reqBedTypeCd, ba.admsStatCd) " +
                "from BdasReq br " +
                "join InfoPt pt on br.id.ptId = pt.ptId " +
                "join BdasEsvy be on br.id.bdasSeq = be.bdasSeq " +
                "left join BdasAdms ba on br.id.bdasSeq = ba.id.bdasSeq " +
                "where br.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
                "$cond " +
                "order by br.updtDttm desc "

        return entityManager.createQuery(query, BdasListDto::class.java)
    }

    fun findBdasList(param: BdasListSearchParam): MutableList<BdasListDto> {
        val (cond, _) = conditionAndOffset(param)
        return queryForBdasList(cond, null).resultList
    }

    fun findBdasListForWeb(param: BdasListSearchParam): MutableList<BdasListDto> {
        val (cond, offset) = conditionAndOffset(param)
        return queryForBdasList(cond, offset).setMaxResults(15).setFirstResult(offset).resultList
    }

    fun countBdasList(param: BdasListSearchParam): Long {
        val (cond, _) = conditionAndOffset(param)
        val query = "select count(br.id.ptId) " +
                "from BdasReq br " +
                "join InfoPt pt on br.id.ptId = pt.ptId " +
                "join BdasEsvy be on br.id.bdasSeq = be.bdasSeq " +
                "left join BdasAdms ba on br.id.bdasSeq = ba.id.bdasSeq " +
                "where br.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
                "$cond "
        return entityManager.createQuery(query).singleResult as Long
    }

    private fun conditionAndOffset(param: BdasListSearchParam): Pair<String, Int> {
        var cond = param.ptNm?.run { " and (pt.ptNm like '%$this%' " } ?: "and (1=1"
        cond += param.rrno1?.run { " or pt.rrno1 like '%$this%' " } ?: ""
        cond += param.mpno?.run { " or pt.mpno like '%$this%') " } ?: ")"

        cond += param.ptTypeCd?.run { " and fn_like_any(br.ptTypeCd, '{%${this.split(',').joinToString("%, %")}%}') = true " } ?: ""
        cond += param.svrtTypeCd?.run { " and br.svrtTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""
        cond += param.gndr?.run { " and pt.gndr in ('${this.split(',').joinToString("', '")}') " } ?: ""
        cond += param.reqBedTypeCd?.run { " and br.reqBedTypeCd in ('${this.split(',').joinToString("', '")}') " } ?: ""
        cond += param.bedStatCd?.run { " and br.bedStatCd in ('${this.split(',').joinToString("', '")}') " } ?: ""

        cond += when {
            param.fromAge != null && param.toAge != null -> " and fn_get_age(pt.rrno1, pt.rrno2) between $this and ${param.toAge} "
            param.fromAge != null && param.toAge == null -> " and fn_get_age(pt.rrno1, pt.rrno2) <= ${param.fromAge} "
            param.fromAge == null && param.toAge != null -> " and fn_get_age(pt.rrno1, pt.rrno2) >= ${param.toAge} "
            else -> ""
        }
        cond += param.period?.run { " and pt.updtDttm > '${Instant.now().minusSeconds(60 * 60 * 24 * this)}' " } ?: ""
        val offset = param.page?.run { this.minus(1).times(15) } ?: 0

        return Pair(cond, offset)
    }

    fun findChrgInst(bedStatCd: String, ptId: String, bdasSeq: Int): String {
        val subQuery = when (bedStatCd) {
            "BAST0003" -> "SELECT a.rgstUserId FROM BdasReq a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            "BAST0004" -> "SELECT a.updtUserId FROM BdasReqAprv a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq AND a.id.asgnReqSeq = 1"
            "BAST0005" -> "SELECT a.updtUserId FROM BdasAprv a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq AND a.aprvYn = 'Y'"
            "BAST0006" -> "SELECT a.updtUserId FROM BdasTrns a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            "BAST0007", "BAST0008" -> "SELECT a.updtUserId FROM BdasAdms a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            else -> "''"
        }

        return try {
            val query = "SELECT iu.instNm from InfoUser iu where iu.id in ($subQuery)"
            entityManager.createQuery(query).singleResult as String
        } catch (ex: jakarta.persistence.NoResultException) {
            // 예외 처리: 결과가 없을 때 빈 문자열을 반환
            ""
        }
    }

    fun findSuspendTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasReqAprvSuspendTimeLine> {
        val query =
            "select new org.sbas.dtos.bdas.BdasReqAprvSuspendTimeLine(" +
                    "'승인대기', " +
                    "ii.instNm, " +
                    "'${TimeLineStatCd.SUSPEND.cdNm}', " +
                    "ii.id, ii.instNm) " +
                    "from BdasReq br " +
                    "join InfoInst ii on ii.dstr1Cd = br.reqDstr1Cd " +
                    "where br.id.ptId = '$ptId' and br.id.bdasSeq = $bdasSeq " +
                    "and ii.instTypeCd = 'ORGN0001' "
        return entityManager.createQuery(query, BdasReqAprvSuspendTimeLine::class.java).resultList
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<CompleteTimeLine> {
        val query =
            "select new org.sbas.dtos.bdas.CompleteTimeLine('병상요청 (' || (case br.inhpAsgnYn when 'Y' then '원내배정' when 'N' then '전원요청' end) || ')', " +
                    "iu.instNm || ' / ' || iu.userNm, br.rgstDttm, br.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                    "iu.instId, iu.instNm, iu.id) " +
                    "from BdasReq br " +
                    "join InfoUser iu on iu.id = br.rgstUserId " +
                    "where br.id.ptId = '$ptId' and br.id.bdasSeq = $bdasSeq"
        return entityManager.createQuery(query, CompleteTimeLine::class.java).resultList
    }
}

@ApplicationScoped
class BdasReqAprvRepository : PanacheRepositoryBase<BdasReqAprv, BdasReqAprvId> {

    fun save(bdasReqAprv: BdasReqAprv): BdasReqAprv {
        persistAndFlush(bdasReqAprv)
        return bdasReqAprv
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<CompleteTimeLine> {
        val query =
            "select new org.sbas.dtos.bdas.CompleteTimeLine(case bra.aprvYn when 'Y' then '승인' when 'N' then '배정불가' end, " +
                    "iu.instNm || ' / ' || iu.userNm, bra.rgstDttm, bra.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                    "iu.instId, iu.instNm, iu.id) " +
                    "from BdasReqAprv bra " +
                    "join InfoUser iu on iu.id = bra.updtUserId " +
                    "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq = 1 "
        return getEntityManager().createQuery(query, CompleteTimeLine::class.java).resultList
    }

    fun findAllByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): List<BdasReqAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list()
    }
}


@ApplicationScoped
class BdasAprvRepository : PanacheRepositoryBase<BdasAprv, BdasAprvId> {

    @Inject
    private lateinit var entityManager: EntityManager

    fun findRefusedBdasAprv(ptId: String, bdasSeq: Int): MutableList<BdasAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq and aprvYn = 'N'").list().toMutableList()
    }

    fun findBdasAprv(ptId: String, bdasSeq: Int): MutableList<BdasAprv>? {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list().toMutableList()
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<TimeLine> {
        val subQuery = " select bra.id.asgnReqSeq " +
                "from BdasReqAprv bra " +
                "left join BdasAprv ba on bra.id.ptId = ba.id.ptId and bra.id.bdasSeq = ba.id.bdasSeq and bra.id.asgnReqSeq = ba.id.asgnReqSeq " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and ba.id.ptId is null and ba.id.bdasSeq is null "
        // TODO
        val query =
            "select new org.sbas.dtos.bdas.BdasAprvCompleteTimeLine(case ba.aprvYn when 'Y' then '배정완료' when 'N' then '배정불가' end, " +
                    "ih.dutyName || ' / ' || iu.userNm, ba.rgstDttm, ba.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                    "ih.hospId, ih.dutyName, iu.id, ba.id.asgnReqSeq) " +
                    "from BdasAprv ba " +
                    "inner join InfoUser iu on iu.id = ba.updtUserId " +
                    "inner join InfoHosp ih on ba.hospId = ih.hospId " +
                    "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq and ba.id.asgnReqSeq not in (${subQuery}) " +
//                    "and iu.jobCd = 'PMGR0003' " +
                    "order by ba.aprvYn "

        val query2 = "select new org.sbas.dtos.bdas.BdasAprvSuspendTimeLine('배정대기', " +
                "bra.reqHospNm || ' / ' || iu.userNm, '${TimeLineStatCd.SUSPEND.cdNm}', " +
                "bra.reqHospId, bra.reqHospNm, iu.id, bra.id.asgnReqSeq) " +
                "from BdasReqAprv bra " +
                "inner join InfoUser iu on iu.instId = bra.reqHospId " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq in (${subQuery}) " +
                "and iu.jobCd = 'PMGR0003' " +
                "order by bra.id.asgnReqSeq "

        val resultList: MutableList<TimeLine> = emptyList<TimeLine>().toMutableList()
        resultList.addAll(entityManager.createQuery(query, BdasAprvCompleteTimeLine::class.java).resultList)
        resultList.addAll(entityManager.createQuery(query2, BdasAprvSuspendTimeLine::class.java).resultList)

        return resultList
    }

    fun findRefuseTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasAprvCompleteTimeLine> {
        val query =
            "select new org.sbas.dtos.bdas.BdasAprvCompleteTimeLine(case ba.aprvYn when 'Y' then '배정완료' when 'N' then '배정불가' end, " +
                    "iu.instNm || ' / ' || iu.userNm, ba.updtDttm, ba.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                    "iu.instId, iu.instNm, iu.id, ba.id.asgnReqSeq) " +
                    "from BdasAprv ba " +
                    "inner join InfoUser iu on iu.id = ba.updtUserId " +
                    "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq and ba.aprvYn = 'N' " +
                    "and iu.jobCd = 'PMGR0003' " +
                    "order by ba.aprvYn "

        return entityManager.createQuery(query, BdasAprvCompleteTimeLine::class.java).resultList
    }

    fun findBdasAprvList(ptId: String, bdasSeq: Int): List<BdasAprv> {
        return find("select ba from BdasAprv ba where exists (select 1 from BdasAprv ba where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq)").list()
    }

    fun findDestinationInfo(ptId: String, bdasSeq: Int): DestinationInfo? {
        val query = """
            select new org.sbas.responses.patient.DestinationInfo(ba.hospId, ih.dutyName, ba.chrgTelno, ih.dutyAddr, 
            ih.wgs84Lat, ih.wgs84Lon, ba.roomNm, ba.deptNm, ba.spclNm, ba.msg) 
            from BdasAprv ba 
            inner join InfoHosp ih on ba.hospId = ih.hospId 
            where ba.id.ptId = '${ptId}' and ba.id.bdasSeq = $bdasSeq and ba.aprvYn = 'Y'
        """.trimIndent()

        val result = entityManager.createQuery(query, DestinationInfo::class.java).resultList
        return if (result.isEmpty()) null else result[0]
    }
}

@ApplicationScoped
class BdasTrnsRepository : PanacheRepositoryBase<BdasTrns, BdasTrnsId> {

    fun findByPtIdAndBdasSeqWithNull(ptId: String, bdasSeq: Int): BdasTrns? {
        return find(
            "id.ptId = '${ptId}' and id.bdasSeq = $bdasSeq",
            Sort.by("id.bdasSeq", Sort.Direction.Descending)
        ).firstResult()
    }

    fun findSuspendTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<CompleteTimeLine> {
        val query = "select new org.sbas.dtos.bdas.CompleteTimeLine('이송중', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "bt.rgstDttm, " +
                "bt.vecno || chr(10) || bt.msg, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}', " +
                "iu.instId, iu.instNm, iu.id) " +
                "from BdasTrns bt " +
                "join InfoUser iu on iu.id = bt.rgstUserId " +
                "where bt.id.ptId = '$ptId' and bt.id.bdasSeq = $bdasSeq"

        return getEntityManager().createQuery(query, CompleteTimeLine::class.java).resultList
    }

    fun findCompleteTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<CompleteTimeLine> {
        val query = "select new org.sbas.dtos.bdas.CompleteTimeLine(" +
                "'이송완료', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "bt.rgstDttm, " +
                "bt.vecno || chr(10) || bt.msg, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}', " +
                "iu.instId, iu.instNm, iu.id) " +
                "from BdasTrns bt " +
                "join InfoUser iu on iu.id = bt.rgstUserId " +
                "where bt.id.ptId = '$ptId' and bt.id.bdasSeq = $bdasSeq"

        return getEntityManager().createQuery(query, CompleteTimeLine::class.java).resultList
    }
}

@ApplicationScoped
class BdasAdmsRepository : PanacheRepositoryBase<BdasAdms, BdasAdmsId> {

    fun findByIdOrderByAdmsSeqDesc(ptId: String, bdasSeq: Int): BdasAdms? {
        return find(
            "id.ptId = '$ptId' and id.bdasSeq = $bdasSeq",
            Sort.by("id.admsSeq", Sort.Direction.Descending)
        ).firstResult()
    }

    fun findSuspendTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<SuspendTimeLine> {
        val query = "select new org.sbas.dtos.bdas.SuspendTimeLine(" +
                "'입원', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "'${TimeLineStatCd.SUSPEND.cdNm}', " +
                "iu.instId, iu.instNm, iu.id) " +
                "from BdasAprv ba " +
                "join InfoUser iu on iu.id = ba.rgstUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq " +
                "and ba.aprvYn = 'Y' "

        return getEntityManager().createQuery(query, SuspendTimeLine::class.java).resultList
    }

    fun findCompleteTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<CompleteTimeLine> {
        val query = "select new org.sbas.dtos.bdas.CompleteTimeLine(" +
                "case ba.admsStatCd when '${AdmsStatCd.IOST0001.name}' then '입원완료' " +
                "when '${AdmsStatCd.IOST0002.name}' then '퇴원완료' when '${AdmsStatCd.IOST0003}' then '자택회송' end, " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "ba.rgstDttm, " +
                "ba.msg, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}', " +
                "iu.instId, iu.instNm, iu.id) " +
                "from BdasAdms ba " +
                "join InfoUser iu on iu.id = ba.rgstUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq " +
                "order by ba.id.admsSeq "

        return getEntityManager().createQuery(query, CompleteTimeLine::class.java).resultList
    }
}
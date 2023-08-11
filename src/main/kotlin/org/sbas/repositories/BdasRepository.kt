package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.col
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import org.sbas.constants.enums.AdmsStatCd
import org.sbas.constants.enums.TimeLineStatCd
import org.sbas.dtos.bdas.BdasListDto
import org.sbas.dtos.bdas.BdasTimeLineDto
import org.sbas.entities.bdas.*
import org.sbas.entities.info.InfoPt
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager

@ApplicationScoped
class BdasEsvyRepository : PanacheRepositoryBase<BdasEsvy, String> {
    fun findByPtIdWithLatestBdasSeq(ptId: String): BdasEsvy? {
        return find("pt_id = '${ptId}'", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }
}

@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId> {

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var queryFactory: QueryFactory

    fun findByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): BdasReq? {
        return find("pt_id = '${ptId}' and bdas_seq = $bdasSeq", Sort.by("bdas_seq", Sort.Direction.Descending)).firstResult()
    }

    fun findBdasList(): MutableList<BdasListDto> {

        val query = "select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId"
        @Suppress("UNCHECKED_CAST")
        val maxBdasSeqList = entityManager.createQuery(query).resultList as MutableList<Int>

//        val subQuery = queryFactory.subquery {
//            select(max(col(BdasReqId::bdasSeq)))
//            from(entity(BdasReq::class))
//            associate(entity(BdasReq::class), BdasReqId::class, on(BdasReq::id))
//            groupBy(col(BdasReqId::ptId))
//        }

        // TODO 검색조건
        val list = queryFactory.listQuery<BdasListDto> {
            selectMulti(
                col(BdasReqId::ptId), col(BdasReqId::bdasSeq), col(InfoPt::ptNm), col(InfoPt::gndr),
                function("fn_get_age", Int::class.java, col(InfoPt::rrno1), col(InfoPt::rrno2)),
                col(InfoPt::bascAddr), col(BdasReq::updtDttm), col(BdasEsvy::diagNm),
                col(BdasReq::bedStatCd),
//                function("fn_get_chrg_inst", String::class.java,
//                    col(BdasReq::bedStatCd), col(BdasReqId::ptId), col(BdasReqId::bdasSeq)
//                ),
                literal("chrgInstNm"),
                col(BdasReq::inhpAsgnYn),
                col(BdasReq::ptTypeCd), col(BdasReq::svrtTypeCd), col(BdasReq::undrDsesCd),
            )
            from(entity(BdasReq::class))
            associate(entity(BdasReq::class), BdasReqId::class, on(BdasReq::id))
            join(entity(InfoPt::class), on { col(BdasReqId::ptId).equal(col(InfoPt::ptId)) })
            join(entity(BdasEsvy::class), on { col(BdasReqId::bdasSeq).equal(col(BdasEsvy::bdasSeq)) })
            whereAnd(
                col(BdasReqId::bdasSeq).`in`(maxBdasSeqList),
            )
            orderBy(
                ExpressionOrderSpec(col(BdasReqId::bdasSeq), ascending = false)
            )
        }

        return list.toMutableList()
    }

    fun findChrgInst(bedStatCd: String, ptId: String, bdasSeq: Int): String {
        val subQuery = when (bedStatCd) {
            "BAST0003" -> "SELECT a.updtUserId FROM BdasReq a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            "BAST0004" -> "SELECT a.updtUserId FROM BdasReqAprv a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq AND a.id.asgnReqSeq = 1"
            "BAST0005" -> "SELECT a.updtUserId FROM BdasAprv a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq AND a.aprvYn = 'Y'"
            "BAST0006" -> "SELECT a.updtUserId FROM BdasTrns a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            "BAST0007", "BAST0008" -> "SELECT a.updtUserId FROM BdasAdms a WHERE a.id.ptId = '$ptId' AND a.id.bdasSeq = $bdasSeq"
            else -> "''"
        }

        return try {
            val query = "SELECT iu.instNm from InfoUser iu where iu.id in ($subQuery)"
            entityManager.createQuery(query).singleResult as String
        } catch (ex: javax.persistence.NoResultException) {
            // 예외 처리: 결과가 없을 때 빈 문자열을 반환
            ""
        }
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto('병상요청 (' || (case br.inhpAsgnYn when 'Y' then '원내배정' when 'N' then '전원요청' end) || ')', " +
                "iu.instNm || ' / ' || iu.userNm, br.updtDttm, br.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                "br.inhpAsgnYn, iu.jobCd, iu.ocpCd, iu.instNm) " +
                "from BdasReq br " +
                "join InfoUser iu on iu.id = br.updtUserId " +
                "where br.id.ptId = '$ptId' and br.id.bdasSeq = $bdasSeq"
        return entityManager.createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findByPtId(ptId: String) = find("from BdasReq where id.ptId='$ptId' order by id.bdasSeq desc").firstResult()
}

@ApplicationScoped
class BdasReqAprvRepository : PanacheRepositoryBase<BdasReqAprv, BdasReqAprvId> {

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case bra.aprvYn when 'Y' then '승인' when 'N' then '배정불가' end, " +
                "iu.instNm || ' / ' || iu.userNm, bra.updtDttm, bra.msg, '${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasReqAprv bra " +
                "join InfoUser iu on iu.id = bra.updtUserId " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq = 1 "
        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findReqAprvListByPtIdAndBdasSeq(ptId: String, bdasSeq: Int): List<BdasReqAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list()
    }
}


@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId> {

    @Inject
    private lateinit var entityManager: EntityManager

    fun findRefusedBdasAprv(ptId: String, bdasSeq: Int): MutableList<BdasAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq and aprvYn = 'N'").list().toMutableList()
    }

    fun findBdasAprv(ptId: String, bdasSeq: Int): MutableList<BdasAprv>? {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list().toMutableList()
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val subQuery = " select bra.id.asgnReqSeq " +
                "from BdasReqAprv bra " +
                "left join BdasAprv ba on bra.id.ptId = ba.id.ptId and bra.id.bdasSeq = ba.id.bdasSeq and bra.id.asgnReqSeq = ba.id.asgnReqSeq " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and ba.id.ptId is null and ba.id.bdasSeq is null "
        // TODO
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case ba.aprvYn when 'Y' then '배정완료' when 'N' then '배정불가' end, " +
                "iu.instNm || ' / ' || iu.userNm, ba.updtDttm, ba.msg, '${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasAprv ba " +
                "inner join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq and ba.id.asgnReqSeq not in (${subQuery}) " +
                "and iu.jobCd = 'PMGR0003' "+
                "order by ba.aprvYn "

        val query2 = "select new org.sbas.dtos.bdas.BdasTimeLineDto('배정대기', " +
                "iu.instNm || ' / ' || iu.userNm, '${TimeLineStatCd.SUSPEND.cdNm}') " +
                "from BdasReqAprv bra " +
                "inner join InfoUser iu on iu.instId = bra.reqHospId " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq in (${subQuery}) " +
                "and iu.jobCd = 'PMGR0003' "+
                "order by bra.id.asgnReqSeq "

        val resultList = entityManager.createQuery(query, BdasTimeLineDto::class.java).resultList
        resultList.addAll(entityManager.createQuery(query2, BdasTimeLineDto::class.java).resultList)

        return resultList
    }

    fun findRefuseTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case ba.aprvYn when 'Y' then '배정완료' when 'N' then '배정불가' end, " +
                "iu.instNm || ' / ' || iu.userNm, ba.updtDttm, ba.msg, '${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasAprv ba " +
                "inner join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq and ba.aprvYn = 'N' " +
                "and iu.jobCd = 'PMGR0003' "+
                "order by ba.aprvYn "

        return entityManager.createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findBdasAprvList(ptId: String, bdasSeq: Int): List<BdasAprv> {
        return find("select ba from BdasAprv ba where exists (select 1 from BdasAprv ba where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq)").list()
    }
}

@ApplicationScoped
class BdasTrnsRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId> {

    fun findSuspendTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto('이송중', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "bt.updtDttm, " +
                "bt.vecno || chr(10) || bt.msg, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}' ) " +
                "from BdasTrns bt " +
                "join InfoUser iu on iu.id = bt.updtUserId " +
                "where bt.id.ptId = '$ptId' and bt.id.bdasSeq = $bdasSeq"

        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findCompleteTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(" +
                "'이송완료', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "bt.updtDttm, " +
                "bt.vecno || chr(10) || bt.msg, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}' ) " +
                "from BdasTrns bt " +
                "join InfoUser iu on iu.id = bt.updtUserId " +
                "where bt.id.ptId = '$ptId' and bt.id.bdasSeq = $bdasSeq"

        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }
}

@ApplicationScoped
class BdasAdmsRepository: PanacheRepositoryBase<BdasAdms, BdasAdmsId> {

    fun findByIdOrderByAdmsSeqDesc(ptId: String, bdasSeq: Int): BdasAdms? {
        return find("pt_id = '$ptId' and bdas_seq = $bdasSeq", Sort.by("adms_seq", Sort.Direction.Descending)).firstResult()
    }

    fun findSuspendTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(" +
                "'입원', " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "'${TimeLineStatCd.SUSPEND.cdNm}') " +
                "from BdasAprv ba " +
                "join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq " +
                "order by ba.aprvYn = 'Y' "

        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findCompleteTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(" +
                "case ba.admsStatCd when '${AdmsStatCd.IOST0001.name}' then '입원완료' " +
                "when '${AdmsStatCd.IOST0002.name}' then '퇴원완료' when '${AdmsStatCd.IOST0003}' then '자택회송' end, " +
                "iu.instNm || ' / ' || iu.userNm, " +
                "ba.updtDttm, " +
                "ba.msg, "
                "'${TimeLineStatCd.COMPLETE.cdNm}' ) " +
                "from BdasAdms ba " +
                "join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq " +
                "order by ba.id.admsSeq "

        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }
}
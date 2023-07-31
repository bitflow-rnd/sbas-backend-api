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

        // TODO 기관, 검색조건
        val list = queryFactory.listQuery<BdasListDto> {
            val getBedAsgnStat =
                function("fn_get_bed_asgn_stat", String::class.java, col(BdasReqId::ptId), col(BdasReqId::bdasSeq))
            selectMulti(
                col(BdasReqId::ptId), col(BdasReqId::bdasSeq), col(InfoPt::ptNm), col(InfoPt::gndr),
                function("fn_get_age", Int::class.java, col(InfoPt::rrno1), col(InfoPt::rrno2)),
                col(InfoPt::bascAddr), col(BdasReq::updtDttm), col(BdasEsvy::diagNm),
                getBedAsgnStat,
                function("fn_get_chrg_inst", String::class.java,
                    getBedAsgnStat, col(BdasReqId::ptId), col(BdasReqId::bdasSeq)
                ),
//                literal("chrgInstNm"),
                col(BdasReq::ptTypeCd), col(BdasReq::svrtTypeCd), col(BdasReq::undrDsesCd)
            )
            from(entity(BdasReq::class))
            associate(entity(BdasReq::class), BdasReqId::class, on(BdasReq::id))
            join(entity(InfoPt::class), on { col(BdasReqId::ptId).equal(col(InfoPt::ptId)) })
            join(entity(BdasEsvy::class), on { col(BdasReqId::bdasSeq).equal(col(BdasEsvy::bdasSeq)) })
            whereAnd(
                col(BdasReqId::bdasSeq).`in`(maxBdasSeqList),
//                col(BdasReqId::bdasSeq).`in`(subQuery.),
            )
            orderBy(
                ExpressionOrderSpec(col(BdasReqId::bdasSeq), ascending = false)
            )
        }
        return list.toMutableList()

//        val query = "select new org.sbas.dtos.bdas.BdasListDto(br.id.ptId, br.id.bdasSeq, pt.ptNm, pt.gndr, fn_get_age(pt.rrno1, pt.rrno2), " +
//                "pt.bascAddr, br.updtDttm, be.diagNm, fn_get_bed_asgn_stat(br.id.ptId, br.id.bdasSeq), '', be.rcptPhc, br.ptTypeCd, br.svrtTypeCd, br.undrDsesCd) " +
//                "from BdasReq br " +
//                "join InfoPt pt on br.id.ptId = pt.ptId " +
//                "join BdasEsvy be on br.id.bdasSeq = be.bdasSeq " +
//                "where br.id.bdasSeq in (select max(id.bdasSeq) as bdasSeq from BdasReq group by id.ptId) " +
//                "order by br.updtDttm desc"
//        return entityManager.createQuery(query, BdasListDto::class.java).resultList
    }

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto('병상요청(' || (case br.inhpAsgnYn when 'Y' then '원내배정' when 'N' then '전원요청' end) || ')', " +
                "iu.instNm || ' / ' || iu.userNm, br.updtDttm, br.msg, '${TimeLineStatCd.COMPLETE.cdNm}', " +
                "br.inhpAsgnYn, iu.jobCd, iu.ocpCd, " +
                "(select instNm from InfoInst where id = 'LG00000001')) " +
                "from BdasReq br " +
                "join InfoUser iu on iu.id = br.rgstUserId " +
                "where br.id.ptId = '$ptId' and br.id.bdasSeq = $bdasSeq"
        return entityManager.createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findBedStat(ptId: String, bdasSeq: Int): String? {
        val query = "select fn_get_bed_asgn_stat('${ptId}', ${bdasSeq}) as test"
        val result = entityManager.createNativeQuery(query).singleResult
        return if (result == "-") {
            null
        } else {
            result as String
        }
    }

    fun findByPtId(ptId: String) = find("from BdasReq where id.ptId='$ptId' order by id.bdasSeq desc").firstResult()
}

@ApplicationScoped
class BdasReqAprvRepository : PanacheRepositoryBase<BdasReqAprv, BdasReqAprvId> {

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case bra.aprvYn when 'Y' then '승인' when 'N' then '불가' end, " +
                "iu.instNm || ' / ' || iu.userNm, bra.updtDttm, bra.msg, '${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasReqAprv bra " +
                "join InfoUser iu on iu.id = bra.updtUserId " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq = 1 "
        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }

    fun findReqAprvList(ptId: String, bdasSeq: Int): List<BdasReqAprv> {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq").list()
    }
}


@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId> {

    @Inject
    private lateinit var entityManager: EntityManager

    fun findApprovedEntity(ptId: String, bdasSeq: Int): BdasAprv? {
        return find("id.ptId = '$ptId' and id.bdasSeq = $bdasSeq and aprvYn = 'Y'").firstResult()
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
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case ba.aprvYn when 'Y' then '배정완료' when 'N' then '배정거절' end, " +
                "iu.instNm || ' / ' || iu.userNm, ba.updtDttm, ba.msg, '${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasAprv ba " +
                "join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq and ba.id.asgnReqSeq not in (${subQuery}) " +
                "and iu.jobCd = 'PMGR0003' "+
                "order by ba.aprvYn "

        val query2 = "select new org.sbas.dtos.bdas.BdasTimeLineDto('배정대기', " +
                "iu.instNm || ' / ' || iu.userNm, '${TimeLineStatCd.SUSPEND.cdNm}') " +
                "from BdasReqAprv bra " +
                "join InfoUser iu on iu.instId = bra.reqHospId " +
                "where bra.id.ptId = '$ptId' and bra.id.bdasSeq = $bdasSeq and bra.id.asgnReqSeq in (${subQuery}) " +
                "and iu.jobCd = 'PMGR0003' "+
                "order by bra.id.asgnReqSeq "

        val resultList = entityManager.createQuery(query, BdasTimeLineDto::class.java).resultList
        resultList.addAll(entityManager.createQuery(query2, BdasTimeLineDto::class.java).resultList)

        return resultList
    }

    fun findBdasAprvList(ptId: String, bdasSeq: Int): List<BdasAprv> {
        return find("select ba from BdasAprv ba where exists (select 1 from BdasAprv ba where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq)").list()
    }
}

@ApplicationScoped
class BdasTrnsRepository: PanacheRepositoryBase<BdasTrns, BdasTrnsId> {

    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {
        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto('이송중', iu.instNm || ' / ' || iu.userNm, " +
                "bt.updtDttm, bt.vecno || chr(10) || bt.msg, '${TimeLineStatCd.SUSPEND.cdNm}' ) " +
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


    fun findTimeLineInfo(ptId: String, bdasSeq: Int): MutableList<BdasTimeLineDto> {

        val query = "select new org.sbas.dtos.bdas.BdasTimeLineDto(case ba.admsStatCd when '${AdmsStatCd.IOST0001}' then '입원완료' " +
                "when '${AdmsStatCd.IOST0002}' then '퇴원완료' when '${AdmsStatCd.IOST0003}' then '자택회송' end, " +
                "'${TimeLineStatCd.COMPLETE.cdNm}') " +
                "from BdasAdms ba " +
                "join InfoUser iu on iu.id = ba.updtUserId " +
                "where ba.id.ptId = '$ptId' and ba.id.bdasSeq = $bdasSeq " +
                "order by ba.id.admsSeq "

        return getEntityManager().createQuery(query, BdasTimeLineDto::class.java).resultList
    }
}
package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoBed
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoHospDetail
import org.sbas.entities.info.InfoUser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

    @Inject
    private lateinit var log:Logger

    @Inject
    private lateinit var queryFactory: QueryFactory

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    fun findInfoHospByHospId(hospId: String): InfoHosp? {
        return find("hospId = '$hospId'").firstResult()
    }

    fun findInfoHospByHpId(hpId: String): InfoHospId {
        val infoHospId = queryFactory.listQuery<InfoHospId> {
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dstrCd1),
                function("fn_get_cd_nm", String::class.java, literal("SIDO"), col(InfoHosp::dstrCd1)),
                col(InfoHosp::attcId),
            )
            from(entity(InfoHosp::class))
            where(
                col(InfoHosp::hpId).equal(hpId)
            )
        }
        return infoHospId[0]
    }

    fun findInfoHosps(param: InfoHospSearchParam): MutableList<InfoHospListDto> {
        val query2 = jpql {
            val medicalStaffCount = select<Long>(
                count(path(InfoUser::id)),
            ).from(
                entity(InfoUser::class),
            ).where(
                path(InfoUser::instId).eq(path(InfoBed::hospId)),
            ).asSubquery()

            selectNew<InfoHospListDto>(
                path(InfoHosp::hospId), path(InfoHosp::hpId), path(InfoHosp::dutyName), path(InfoHosp::dutyDivNam),
                path(InfoHosp::dstrCd1), path(InfoHosp::dstrCd2), path(InfoHosp::dutyTel1), path(InfoHosp::dutyTel1), path(InfoBed::updtDttm),
                path(InfoBed::gnbdIcu), path(InfoBed::npidIcu), path(InfoBed::gnbdSvrt), path(InfoBed::gnbdSmsv), path(InfoBed::gnbdModr),
                path(InfoBed::ventilator), path(InfoBed::ventilatorPreemie), path(InfoBed::incubator), path(InfoBed::ecmo), path(InfoBed::highPressureOxygen),
                path(InfoBed::ct), path(InfoBed::mri), path(InfoBed::highPressureOxygen), path(InfoBed::bodyTemperatureControl),
                path(InfoBed::emrgncyNrmlBed), path(InfoBed::ngtvIsltnChild), path(InfoBed::nrmlIsltnChild),
                path(InfoBed::nrmlChildBed), path(InfoBed::emrgncyNrmlIsltnBed), medicalStaffCount,
            ).from(
                entity(InfoHosp::class),
                join(InfoBed::class).on(path(InfoHosp::hospId).eq(path(InfoBed::hospId)))
            ).whereAnd(
                param.hospId?.run { path(InfoHosp::hospId).like("%$this%") },
                param.dutyName?.run { path(InfoHosp::dutyName).like("%$this%") },
                param.dstrCd1?.run { path(InfoHosp::dstrCd1).equal(this) },
                param.dstrCd2?.run { path(InfoHosp::dstrCd2).equal(this) },
                param.dutyDivNam?.run { path(InfoHosp::dutyDivNam).`in`(this) },
            ).orderBy(
                path(InfoBed::gnbdSvrt).desc(),
                path(InfoBed::gnbdIcu).desc(),
                path(InfoBed::npidIcu).desc(),
                path(InfoHosp::hospId).asc(),
            )
        }

        val offset = param.page?.run { this.minus(1).times(15) } ?: 0
        val createQuery = entityManager.createQuery(query2, context).setMaxResults(15).setFirstResult(offset)

        return createQuery.resultList
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
                col(InfoHosp::dutyName).like("%보건소"),
                dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
                dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
            )
        }

        return healthCenterList
    }

    fun findMediOrgan(dstrCd1: String?, dstrCd2: String?): List<InfoInstResponse> {
        val list = queryFactory.listQuery<InfoInstResponse> {
            selectMulti(
                col(InfoHosp::hospId), literal("ORGN0004"), col(InfoHosp::dutyName),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2),
            )
            from(entity(InfoHosp::class))
            join(entity(InfoBed::class), on { col(InfoHosp::hospId).equal(col(InfoBed::hospId)) })
            whereAnd(
                dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
                dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
            )
        }

        return list.toMutableList()
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
class InfoHospDetailRepository : PanacheRepositoryBase<InfoHospDetail, String> {

    fun updateDetailInfo() {

    }

}
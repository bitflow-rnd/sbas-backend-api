package org.sbas.repositories

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoBed
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoHospDetail
import org.sbas.entities.info.InfoUser

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

    @Inject
    private lateinit var log:Logger

    @Inject
    private lateinit var entityManager: EntityManager

    @Inject
    private lateinit var context: JpqlRenderContext

    fun findInfoHospByHospId(hospId: String): InfoHosp? {
        return find("hospId = '$hospId'").firstResult()
    }

    fun findInfoHospByHpId(hpId: String): InfoHospId {
        val query = jpql {
            selectNew<InfoHospId>(
                path(InfoHosp::hospId), path(InfoHosp::hpId), path(InfoHosp::dstrCd1),
                function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoHosp::dstrCd1)),
                path(InfoHosp::attcId),
            ).from(
                entity(InfoHosp::class)
            ).where(
                path(InfoHosp::hpId).eq(hpId)
            )
        }

        return entityManager.createQuery(query, context).singleResult
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
                path(InfoBed::nrmlChildBed), path(InfoBed::emrgncyNgtvIsltnBed), path(InfoBed::emrgncyNrmlIsltnBed),
                path(InfoBed::isltnMedAreaNgtvIsltnBed), path(InfoBed::isltnMedAreaNrmlIsltnBed), path(InfoBed::cohtBed),
                medicalStaffCount,
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
        val query = jpql {
            select(
                count(entity(InfoHosp::class))
            ).from(
                entity(InfoHosp::class),
                join(InfoBed::class).on(path(InfoHosp::hospId).eq(path(InfoBed::hospId)))
            ).whereAnd(
                param.hospId?.run { path(InfoHosp::hospId).like("%$this%") },
                param.dutyName?.run { path(InfoHosp::dutyName).like("%$this%") },
                param.dstrCd1?.run { path(InfoHosp::dstrCd1).equal(this) },
                param.dstrCd2?.run { path(InfoHosp::dstrCd2).equal(this) },
                param.dutyDivNam?.run { path(InfoHosp::dutyDivNam).`in`(this) },
            )
        }

        return entityManager.createQuery(query, context).singleResult.toInt()
    }

    fun findByHospIdList(hospList: List<String>): MutableList<InfoHospWithUser> {
        val query = jpql {
            selectNew<InfoHospWithUser>(
                path(InfoHosp::hospId), path(InfoHosp::dutyName),
                path(InfoUser::instId), path(InfoUser::instNm), path(InfoUser::id)
            ).from(
                entity(InfoHosp::class),
                join(InfoUser::class).on(path(InfoHosp::hospId).eq(path(InfoUser::instId)))
            ).whereAnd(
                path(InfoUser::instId).`in`(hospList),
                path(InfoUser::jobCd).eq("PMGR0003"),
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findAvalHospListByDstrCd1(dstrCd1: String): MutableList<AvalHospDto> {
        val query = jpql {
            selectNew<AvalHospDto>(
                path(InfoHosp::hospId), path(InfoHosp::dutyName), path(InfoHosp::wgs84Lon), path(InfoHosp::wgs84Lat),
                path(InfoHosp::dutyAddr), path(InfoBed::gnbdIcu), path(InfoBed::npidIcu), path(InfoBed::gnbdSvrt),
                path(InfoBed::gnbdSmsv), path(InfoBed::gnbdModr),
                path(InfoBed::ventilator), path(InfoBed::ventilatorPreemie), path(InfoBed::incubator), path(InfoBed::ecmo),
                path(InfoBed::highPressureOxygen), path(InfoBed::ct), path(InfoBed::mri), path(InfoBed::highPressureOxygen),
                path(InfoBed::bodyTemperatureControl),
            ).from(
                entity(InfoHosp::class),
                join(InfoBed::class).on(path(InfoHosp::hospId).eq(path(InfoBed::hospId)))
            ).whereAnd(
                path(InfoHosp::dstrCd1).eq(dstrCd1),
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findPubHealthCenter(dstrCd1: String?, dstrCd2: String?): List<InfoInstResponse> {
        val query = jpql {
            selectNew<InfoInstResponse>(
                path(InfoHosp::hospId), stringLiteral("ORGN0003"), path(InfoHosp::dutyName),
                path(InfoHosp::dstrCd1), path(InfoHosp::dstrCd2),
            ).from(
                entity(InfoHosp::class)
            ).whereAnd(
                path(InfoHosp::dutyDivNam).eq("보건소"),
                path(InfoHosp::dutyName).like("%보건소"),
                dstrCd1?.run { path(InfoHosp::dstrCd1).equal(this) },
                dstrCd2?.run { path(InfoHosp::dstrCd2).equal(this) },
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    fun findMediOrgan(dstrCd1: String?, dstrCd2: String?): List<InfoInstResponse> {
        val query = jpql {
            selectNew<InfoInstResponse>(
                path(InfoHosp::hospId), stringLiteral("ORGN0004"), path(InfoHosp::dutyName),
                path(InfoHosp::dstrCd1), path(InfoHosp::dstrCd2),
            ).from(
                entity(InfoHosp::class),
                join(InfoBed::class).on(path(InfoHosp::hospId).equal(path(InfoBed::hospId)))
            ).whereAnd(
                dstrCd1?.run { path(InfoHosp::dstrCd1).equal(this) },
                dstrCd2?.run { path(InfoHosp::dstrCd2).equal(this) },
            )
        }

        return entityManager.createQuery(query, context).resultList
    }


//    private fun CriteriaQueryDsl<*>.whereAnd(param: InfoHospSearchParam) {
//        whereAnd(
//            param.hospId?.run { col(InfoHosp::hospId).like("%$this%") },
//            param.dutyName?.run { col(InfoHosp::dutyName).like("%$this%") },
//            param.dstrCd1?.run { col(InfoHosp::dstrCd1).equal(this) },
//            param.dstrCd2?.run { col(InfoHosp::dstrCd2).equal(this) },
//            param.dutyDivNam?.run { col(InfoHosp::dutyDivNam).`in`(this) },
//        )
//    }
}

@ApplicationScoped
class InfoHospDetailRepository : PanacheRepositoryBase<InfoHospDetail, String> {

    fun updateDetailInfo() {

    }

}
package org.sbas.repositories

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.subquery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.dtos.info.*
import org.sbas.entities.info.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

    @Inject
    private lateinit var log:Logger

    @Inject
    private lateinit var queryFactory: QueryFactory

    fun findInfoHospByHospId(hospId: String): InfoHosp? {
        return find("hosp_id = '$hospId'").firstResult()
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
        val infoHosps = queryFactory.listQuery<InfoHospListDto> {
            val medicalStaffCount = queryFactory.subquery {
                select(
                    count(col(InfoUser::id))
                )
                from(entity(InfoUser::class))
                groupBy(col(InfoUser::id))
                where(col(InfoUser::instId).equal(col(InfoBed::hospId)))
            }
            selectMulti(
                col(InfoHosp::hospId), col(InfoHosp::hpId), col(InfoHosp::dutyName), col(InfoHosp::dutyDivNam),
                col(InfoHosp::dstrCd1), col(InfoHosp::dstrCd2), col(InfoHosp::dutyTel1), col(InfoHosp::dutyTel1), col(InfoBed::updtDttm),
                col(InfoBed::gnbdIcu), col(InfoBed::npidIcu), col(InfoBed::gnbdSvrt),
                col(InfoBed::gnbdSmsv), col(InfoBed::gnbdModr),
                col(InfoBed::ventilator), col(InfoBed::ventilatorPreemie), col(InfoBed::incubator), col(InfoBed::ecmo),
                col(InfoBed::highPressureOxygen), col(InfoBed::ct), col(InfoBed::mri), col(InfoBed::highPressureOxygen),
                col(InfoBed::bodyTemperatureControl),
                col(InfoBed::emrgncyNrmlBed), col(InfoBed::ngtvIsltnChild), col(InfoBed::nrmlIsltnChild),
                col(InfoBed::nrmlChildBed), col(InfoBed::emrgncyNrmlIsltnBed), medicalStaffCount
            )
            from(entity(InfoHosp::class))
            join(entity(InfoBed::class), on { col(InfoHosp::hospId).equal(col(InfoBed::hospId)) })
            limit(15)
            param.page?.run { offset(this.minus(1).times(15)) }
            whereAnd(param)
            orderBy(
                ExpressionOrderSpec(col(InfoBed::gnbdSvrt), ascending = false),
                ExpressionOrderSpec(col(InfoBed::gnbdIcu), ascending = false),
                ExpressionOrderSpec(col(InfoBed::npidIcu), ascending = false),
                ExpressionOrderSpec(col(InfoHosp::hospId), ascending = true),
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
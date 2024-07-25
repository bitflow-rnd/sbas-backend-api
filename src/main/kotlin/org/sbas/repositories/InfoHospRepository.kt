package org.sbas.repositories

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.hibernate.extension.createQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import org.jboss.logging.Logger
import org.sbas.dtos.bdas.AvalHospListRequest
import org.sbas.dtos.info.*
import org.sbas.entities.info.InfoBed
import org.sbas.entities.info.InfoHosp
import org.sbas.entities.info.InfoHospDetail
import org.sbas.entities.info.InfoUser

@ApplicationScoped
class InfoHospRepository : PanacheRepositoryBase<InfoHosp, String> {

  @Inject
  private lateinit var log: Logger

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
        path(InfoHosp::hospId), path(InfoHosp::hpId), path(InfoHosp::dstr1Cd),
        function(String::class, "fn_get_cd_nm", stringLiteral("SIDO"), path(InfoHosp::dstr1Cd)),
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
        path(InfoHosp::hospId),
        path(InfoHosp::hpId),
        path(InfoHosp::dutyName),
        path(InfoHosp::dutyDivNam),
        path(InfoHosp::dstr1Cd),
        path(InfoHosp::dstr2Cd),
        path(InfoHosp::dutyTel1),
        path(InfoHosp::dutyTel1),
        path(InfoBed::updtDttm),
        path(InfoBed::gnbdIcu),
        path(InfoBed::npidIcu),
        path(InfoBed::gnbdSvrt),
        path(InfoBed::gnbdSmsv),
        path(InfoBed::gnbdModr),
        path(InfoBed::ventilator),
        path(InfoBed::ventilatorPreemie),
        path(InfoBed::incubator),
        path(InfoBed::ecmo),
        path(InfoBed::highPressureOxygen),
        path(InfoBed::ct),
        path(InfoBed::mri),
        path(InfoBed::highPressureOxygen),
        path(InfoBed::bodyTemperatureControl),
        path(InfoBed::emrgncyNrmlBed),
        path(InfoBed::ngtvIsltnChild),
        path(InfoBed::nrmlIsltnChild),
        path(InfoBed::nrmlChildBed),
        path(InfoBed::emrgncyNgtvIsltnBed),
        path(InfoBed::emrgncyNrmlIsltnBed),
        path(InfoBed::isltnMedAreaNgtvIsltnBed),
        path(InfoBed::isltnMedAreaNrmlIsltnBed),
        path(InfoBed::cohtBed),
        medicalStaffCount,
      ).from(
        entity(InfoHosp::class),
        join(InfoBed::class).on(path(InfoHosp::hospId).eq(path(InfoBed::hospId)))
      ).whereAnd(
        param.hospId?.run { path(InfoHosp::hospId).like("%$this%") },
        param.dutyName?.run { path(InfoHosp::dutyName).like("%$this%") },
        param.dstr1Cd?.run { path(InfoHosp::dstr1Cd).equal(this) },
        param.dstr2Cd?.run { path(InfoHosp::dstr2Cd).equal(this) },
        param.dutyDivNam?.run { path(InfoHosp::dutyDivNam).`in`(this.split(",")) },
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
        param.dstr1Cd?.run { path(InfoHosp::dstr1Cd).equal(this) },
        param.dstr2Cd?.run { path(InfoHosp::dstr2Cd).equal(this) },
        param.dutyDivNam?.run { path(InfoHosp::dutyDivNam).`in`(this.split(",")) },
      )
    }

    return entityManager.createQuery(query, context).singleResult.toInt()
  }

  fun findByHospIdList(hospList: List<String>): List<InfoHosp> {

    return list("hospId in ?1", hospList)

    //        val query = jpql {
    //            selectNew<InfoHospWithUser>(
    //                path(InfoHosp::hospId), path(InfoHosp::dutyName)
    //            ).from(
    //                entity(InfoHosp::class),
    //                join(InfoUser::class).on(path(InfoHosp::hospId).eq(path(InfoUser::instId)))
    //            ).whereAnd(
    //                path(InfoHosp::hospId).`in`(hospList),
    //                path(InfoUser::jobCd).eq("PMGR0003"),
    //            )
    //        }
    //
    //        return entityManager.createQuery(query, context).resultList
  }

  fun findAvalHospList(dstr1Cd: String, dstr2Cd: String?, param: AvalHospListRequest): MutableList<AvalHospDto> {
    val query = jpql {
      selectNew<AvalHospDto>(
        path(InfoHosp::hospId),
        path(InfoHosp::dutyName),
        path(InfoHosp::dutyDivNam),
        path(InfoHosp::wgs84Lon),
        path(InfoHosp::wgs84Lat),
        path(InfoHosp::dutyAddr),
        path(InfoBed::gnbdIcu),
        path(InfoBed::npidIcu),
        path(InfoBed::gnbdSvrt),
        path(InfoBed::gnbdSmsv),
        path(InfoBed::gnbdModr),
        path(InfoBed::ventilator),
        path(InfoBed::ventilatorPreemie),
        path(InfoBed::incubator),
        path(InfoBed::ecmo),
        path(InfoBed::highPressureOxygen),
        path(InfoBed::ct),
        path(InfoBed::mri),
        path(InfoBed::highPressureOxygen),
        path(InfoBed::bodyTemperatureControl),
      ).from(
        entity(InfoHosp::class),
        join(InfoBed::class).on(path(InfoHosp::hospId).eq(path(InfoBed::hospId)))
      ).whereAnd(
        path(InfoHosp::dstr1Cd).eq(dstr1Cd),
        param.dutyName?.run { path(InfoHosp::dutyName).like("%$this%") },
        path(InfoHosp::dutyName).`in`("경북대학교병원", "칠곡경북대학교병원", "대구파티마병원", "대구의료원"),
      )
    }

    return entityManager.createQuery(query, context).resultList
  }

  fun findMediOrgan(dstr1Cd: String?, dstr2Cd: String?): List<InfoInstResponse> {
    val query = jpql {
      selectNew<InfoInstResponse>(
        path(InfoHosp::hospId), stringLiteral("ORGN0004"), path(InfoHosp::dutyName),
        path(InfoHosp::dstr1Cd), path(InfoHosp::dstr2Cd),
      ).from(
        entity(InfoHosp::class),
        join(InfoBed::class).on(path(InfoHosp::hospId).equal(path(InfoBed::hospId)))
      ).whereAnd(
        dstr1Cd?.run { path(InfoHosp::dstr1Cd).equal(this) },
        dstr2Cd?.run { path(InfoHosp::dstr2Cd).equal(this) },
      )
    }

    return entityManager.createQuery(query, context).resultList
  }

  fun findListByIds(list: List<String>): List<InfoHosp> {
    return list("hospId in ?1", list)
  }
}

@ApplicationScoped
class InfoHospDetailRepository : PanacheRepositoryBase<InfoHospDetail, String> {

  fun findByHospId(hospId: String): InfoHospDetail? {
    return find("hospId = '$hospId'").firstResult()
  }
}
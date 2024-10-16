package org.sbas.component

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.sbas.dtos.bdas.BdasTrnsSaveRequest
import org.sbas.entities.info.InfoCrew
import org.sbas.repositories.InfoCrewRepository
import org.sbas.repositories.InfoInstRepository

@ApplicationScoped
class InfoCrewComponent(
  private val infoCrewRepository: InfoCrewRepository,
  private val infoInstRepository: InfoInstRepository,
) {

  @Transactional
  fun saveInfoCrew(request: BdasTrnsSaveRequest): List<InfoCrew> {
    val latestCrewId = infoCrewRepository.findLatestCrewId(request.instId)
    val infoCrews = mutableListOf<InfoCrew>()
    request.toInfoCrewList().forEachIndexed { idx, infoCrew ->
      if (infoCrew != null) {
        // infoCrew가 null이 아닌 경우
        val findInfoCrew = infoCrewRepository.findInfoCrew(request.instId, infoCrew.id.crewId)
        findInfoCrew?.update(infoCrew)
        infoCrews.add(infoCrew)
      } else {
        val infoCrewSaveReq = request.toInfoCrewSaveReqList()[idx]
        val newInfoCrew = infoCrewSaveReq?.toEntityForInsert(latestCrewId + 1)
        if (newInfoCrew != null) {
          infoCrewRepository.persist(newInfoCrew)
          infoCrews.add(newInfoCrew)
        }
      }
    }

    return infoCrews
  }

  fun saveVehicleInfo(instId: String, vecno: String?) {
    val fireStatn = infoInstRepository.findFireStatn(instId)
    fireStatn.updateFireStatnVecno(vecno)
  }

}
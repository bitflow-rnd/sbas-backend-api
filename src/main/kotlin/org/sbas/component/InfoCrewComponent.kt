package org.sbas.component

import jakarta.enterprise.context.ApplicationScoped
import org.sbas.dtos.info.InfoCrewSaveReq
import org.sbas.repositories.InfoCrewRepository
import org.sbas.repositories.InfoInstRepository

@ApplicationScoped
class InfoCrewComponent(
    private val infoCrewRepository: InfoCrewRepository,
    private val infoInstRepository: InfoInstRepository,
) {

    fun saveInfoCrew(list: List<InfoCrewSaveReq?>, instId: String) {
        val latestCrewId = infoCrewRepository.findLatestCrewId(instId)

        val infoCrews = list.filterNotNull().mapIndexed { idx, it ->
            it.toEntityForInsert(latestCrewId + 1 + idx)
        }

        infoCrews.forEach {
            infoCrewRepository.persist(it)
        }
    }

    fun saveVehicleInfo(instId: String, vecno: String?) {
        val fireStatn = infoInstRepository.findFireStatn(instId)
        fireStatn.updateFireStatnVecno(vecno)
    }

}
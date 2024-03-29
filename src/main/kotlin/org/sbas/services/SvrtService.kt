package org.sbas.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.sbas.entities.svrt.SvrtAnly
import org.sbas.entities.svrt.SvrtAnlyId
import org.sbas.entities.svrt.SvrtColl
import org.sbas.entities.svrt.SvrtCollId
import org.sbas.repositories.SvrtAnlyRepository
import org.sbas.repositories.SvrtCollRepository
import org.sbas.repositories.SvrtPtRepository
import org.sbas.responses.CommonResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class SvrtService {

    @Inject
    private lateinit var svrtPtRepository: SvrtPtRepository

    @Inject
    private lateinit var svrtAnlyRepository: SvrtAnlyRepository

    @Inject
    private lateinit var svrtCollRepository: SvrtCollRepository

    fun getLastSvrtAnlyByPtId(ptId: String): CommonResponse<*> {
        return CommonResponse(svrtAnlyRepository.getSvrtAnlyByPtId(ptId))
    }

    @Transactional
    fun saveSvrtAnly(anly: SvrtAnly) {
        svrtAnlyRepository.persist(anly)
    }

    fun getSvrtCollByPidAndMsreDt(pid: String): List<SvrtColl>? {
        return svrtCollRepository.findByPtIdAndMsreDt(pid)
    }

    /**
     * Converts data from DB to request data format
     */
    fun getSvrtRequestData(svrtCollList: List<SvrtColl>): String {
        val requestMap = mapOf(
            "BUN" to mutableMapOf<String, Float>(),
            "Creatinine" to mutableMapOf(),
            "Hemoglobin" to mutableMapOf(),
            "LDH" to mutableMapOf(),
            "Lymphocytes" to mutableMapOf(),
            "Neutrophils" to mutableMapOf(),
            "Platelet count" to mutableMapOf(),
            "Potassium" to mutableMapOf(),
            "Sodium" to mutableMapOf(),
            "WBC Count" to mutableMapOf(),
            "CRP" to mutableMapOf(),
            "BDTEMP" to mutableMapOf(),
            "BREATH" to mutableMapOf(),
            "DBP" to mutableMapOf(),
            "PULSE" to mutableMapOf(),
            "SBP" to mutableMapOf(),
            "SPO2" to mutableMapOf(),
            "Oxygen apply" to mutableMapOf<String, String>()
        )
        var msreDt: String
        svrtCollList.forEach {
            msreDt = it.id!!.msreDt
            (requestMap["BUN"] as HashMap<String, Float>)[msreDt] = it.bun!!.toFloat()
            (requestMap["Creatinine"] as HashMap<String, Float>)[msreDt] = it.cre!!.toFloat()
            (requestMap["Hemoglobin"] as HashMap<String, Float>)[msreDt] = it.hem!!.toFloat()
            (requestMap["LDH"] as HashMap<String, Float>)[msreDt] = it.ldh!!.toFloat()
            (requestMap["Lymphocytes"] as HashMap<String, Float>)[msreDt] = it.lym!!.toFloat()
            (requestMap["Neutrophils"] as HashMap<String, Float>)[msreDt] = it.neu!!.toFloat()
            (requestMap["Platelet count"] as HashMap<String, Float>)[msreDt] = it.pla!!.toFloat()
            (requestMap["Potassium"] as HashMap<String, Float>)[msreDt] = it.pot!!.toFloat()
            (requestMap["Sodium"] as HashMap<String, Float>)[msreDt] = it.sod!!.toFloat()
            (requestMap["WBC Count"] as HashMap<String, Float>)[msreDt] = it.wbc!!.toFloat()
            (requestMap["CRP"] as HashMap<String, Float>)[msreDt] = it.crp!!.toFloat()
            (requestMap["BDTEMP"] as HashMap<String, Float>)[msreDt] = it.bdtp!!.toFloat()
            (requestMap["BREATH"] as HashMap<String, Float>)[msreDt] = it.resp!!.toFloat()
            (requestMap["DBP"] as HashMap<String, Float>)[msreDt] = it.dbp!!.toFloat()
            (requestMap["PULSE"] as HashMap<String, Float>)[msreDt] = it.hr!!.toFloat()
            (requestMap["SBP"] as HashMap<String, Float>)[msreDt] = it.sbp!!.toFloat()
            (requestMap["SPO2"] as HashMap<String, Float>)[msreDt] = it.spo2!!.toFloat()
            (requestMap["Oxygen apply"] as HashMap<String, String>)[msreDt] = it.oxygen!!.toString()

        }
        val json = ObjectMapper().writeValueAsString(requestMap)

        return JSONObject.quote(json.toString())
    }

    fun getLastAnlySeqValue(): Int? {
        return svrtAnlyRepository.getLastAnlySeqValue()
    }

}
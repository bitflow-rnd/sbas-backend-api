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
import org.sbas.utils.StringUtils.Companion.getYyyyMmDdWithHyphen
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class SvrtService {

    @Inject
    private lateinit var svrtPtRepository: SvrtPtRepository

    @Inject
    private lateinit var svrtAnlyRepository: SvrtAnlyRepository

    @Inject
    private lateinit var svrtCollRepository: SvrtCollRepository


    fun getSvrtAnlyById(id: SvrtAnlyId): SvrtAnly? {
        return svrtAnlyRepository.findById(id)
    }

    @Transactional
    fun saveSvrtAnly(anly: SvrtAnly) {
        svrtAnlyRepository.persist(anly)
    }

    fun deleteSvrtAnlyById(id: SvrtAnlyId): Boolean {
        return svrtAnlyRepository.deleteById(id)
    }


    fun getSvrtCollById(id: SvrtCollId): SvrtColl? {
        System.out.println(5)
        return svrtCollRepository.findById(id)
    }

    fun getSvrtCollByPidAndMsreDt(pid: String): List<SvrtColl>? {
        return svrtCollRepository.findByPtIdAndMsreDt(pid)
    }

    /**
     * Converts data from DB to request data format
     */
    fun getSvrtRequestData(svrtCollList: List<SvrtColl>): String {
        val requestMap = mapOf(
            "ALT" to mutableMapOf<String, Float>(),
            "AST" to mutableMapOf(),
            "BUN" to mutableMapOf(),
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
            "SPO2" to mutableMapOf()
        )
        var msreDt: String
        svrtCollList.forEach {
            msreDt = getYyyyMmDdWithHyphen(it.id!!.msreDt)
            (requestMap["ALT"] as HashMap<String, Float>)[msreDt] = it.alt!!.toFloat()
            (requestMap["AST"] as HashMap<String, Float>)[msreDt] = it.ast!!.toFloat()
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

        }
        val json = ObjectMapper().writeValueAsString(requestMap)

        return JSONObject.quote(json.toString())
    }

    fun getLastAnlySeqValue(): Int? {
        return svrtAnlyRepository.getLastAnlySeqValue()
    }

    fun saveSvrtColl(coll: SvrtColl) {
        svrtCollRepository.persist(coll)
    }

    fun deleteSvrtCollById(id: SvrtCollId): Boolean {
        return svrtCollRepository.deleteById(id)
    }

}
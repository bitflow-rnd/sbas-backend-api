package org.sbas.restparameters

import com.fasterxml.jackson.annotation.JsonProperty
import org.sbas.entities.svrt.SvrtColl

data class NubisonSvrtAnlyRequest(
  @field:JsonProperty("content_type") val contentType: String = "pd",
  @field:JsonProperty("inputs") val inputs: List<NubisonSvrtAnlyInputData> = listOf()
)

data class NubisonSvrtAnlyInputData(
  @field:JsonProperty("data") val data: List<String> = listOf(),
  @field:JsonProperty("datatype") val dataType: String = "BYTES",
  @field:JsonProperty("name") val name: String = "data",
  @field:JsonProperty("parameters") val parameters: NubisonSvrtAnlyParameters = NubisonSvrtAnlyParameters(),
  @field:JsonProperty("shape") val shape: List<Int> = listOf()
)

data class NubisonSvrtAnlyParameters(
  @field:JsonProperty("content_type") val contentType: String = "str"
)

data class MntrInfo(
  @field:JsonProperty("BUN") val bun: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Creatinine") val cre: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Hemoglobin") val hem: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("LDH") val ldh: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Lymphocytes") val lym: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Neutrophils") val neu: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Platelet count") val pla: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Potassium") val pot: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Sodium") val sod: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("WBC Count") val wbc: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("CRP") val crp: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("BDTEMP") val bdtp: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("BREATH") val resp: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("DBP") val dbp: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("PULSE") val hr: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("SBP") val sbp: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("SPO2") val spo2: MutableMap<String, Float> = mutableMapOf(),
  @field:JsonProperty("Oxygen apply") val oxygenApply: MutableMap<String, String> = mutableMapOf(),
) {
  fun inputData(date: String, svrtColl: SvrtColl) {
    this.apply {
      bun[date] = svrtColl.bun!!.toFloat()
      cre[date] = svrtColl.cre!!.toFloat()
      hem[date] = svrtColl.hem!!.toFloat()
      ldh[date] = svrtColl.ldh!!.toFloat()
      lym[date] = svrtColl.lym!!.toFloat()
      neu[date] = svrtColl.neu!!.toFloat()
      pla[date] = svrtColl.pla!!.toFloat()
      pot[date] = svrtColl.pot!!.toFloat()
      sod[date] = svrtColl.sod!!.toFloat()
      wbc[date] = svrtColl.wbc!!.toFloat()
      crp[date] = svrtColl.crp!!.toFloat()
      bdtp[date] = svrtColl.bdtp!!.toFloat()
      resp[date] = svrtColl.resp!!.toFloat()
      dbp[date] = svrtColl.dbp!!.toFloat()
      hr[date] = svrtColl.hr!!.toFloat()
      sbp[date] = svrtColl.sbp!!.toFloat()
      spo2[date] = svrtColl.spo2!!.toFloat()
      oxygenApply[date] = svrtColl.oxygenApply!!
    }
  }
}

data class NubisonAiSeverityAnalysisResponse(

  @JsonProperty("model_name")
  var modelName: String,

  @JsonProperty("model_version")
  var modelVersion: String,

  @JsonProperty("id")
  var id: String,

  @JsonProperty("parameters")
  var parameters: String?,

  @JsonProperty("outputs")
  var outputs: List<OutputData>
)

data class OutputData(

  @JsonProperty("name")
  var name: String,

  @JsonProperty("shape")
  var shape: List<Int>,

  @JsonProperty("datatype")
  var dataType: String,

  @JsonProperty("parameters")
  var parameters: String?,

  @JsonProperty("data")
  var data: List<Float>
)
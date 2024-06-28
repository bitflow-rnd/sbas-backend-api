package org.sbas.dtos

import org.sbas.utils.annotation.NoArg

@NoArg
data class SrvrSstmBsic (
  var srvrLnchProf: String? = null,
  var srvrOsNm: String? = null,
  var srvrUpldDir: String? = null,
  var srvrUpldFils: List<String>? = null,
  var srvrInnrIp: String? = null,
  var srvrRmotIp: String? = null,
  var srvrLcalIp: String? = null
)
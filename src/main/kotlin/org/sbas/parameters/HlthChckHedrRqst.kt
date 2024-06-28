package org.sbas.parameters

import org.sbas.utils.annotation.NoArg

@NoArg
data class HlthChckHedrRqst(
  var userAgnt: String? = null,
  var ctnt: String? = null,
  var ctry: String? = null,
  var regn: String? = null,
  var city: String? = null,
  var plfm: String? = null,
  var lang: String? = null,
  var host: String? = null,
  var connIp: String? = null,
  var tmzn: String? = null,
  var refr: String? = null,
  var xRealIp: String? = null,
  var acptLang: String? = null,
  var clntIp: String? = null,
  var rmotIp: String? = null,
  var brwsCtry: String? = null
)

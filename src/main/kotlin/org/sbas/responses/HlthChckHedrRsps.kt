package org.sbas.responses

import org.sbas.parameters.HlthChckHedrRqst
import org.sbas.utils.annotation.NoArg

@NoArg
data class HlthChckHedrRsps(val hedr: HlthChckHedrRqst) {
  var reqUserAgnt: String? = hedr.userAgnt
  var rqstCtnt: String? = hedr.ctnt
  var rqstCtry: String? = hedr.ctry
  var rqstRegn: String? = hedr.regn
  var rqstCity: String? = hedr.city
  var rqstPlfm: String? = hedr.plfm
  var rqstLang: String? = hedr.lang
  var rqstHost: String? = hedr.host
  var rqstTmzn: String? = hedr.tmzn
  var rqstRefr: String? = hedr.refr
  var rqstConnIp: String? = hedr.connIp
  var rqstXRealIp: String? = hedr.xRealIp
  var rqstRmotIp: String? = hedr.rmotIp
  var rqstClntIp: String? = hedr.clntIp
}
package id.g8id.api.rsps

import org.sbas.dtos.SrvrSstmBsic
import org.sbas.responses.HlthChckHedrRsps
import org.sbas.utils.annotation.NoArg

@NoArg
data class HlthChckDeftRsps(
  // 1. (common) request header extracted client info
  var clnt: HlthChckHedrRsps? = null
) {
  // 2. DB connection check
  var dbRowCnt: Long? = null
  // 3. server internal basic info
  var srvr: SrvrSstmBsic? = null
}


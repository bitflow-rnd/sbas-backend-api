package id.g8id.api.rsps

import org.sbas.responses.HlthChckHedrRsps
import org.sbas.utils.annotation.NoArg

@NoArg
data class HlthChckMinmRsps(
  // 1. (common) request header extracted client info
  var clnt: HlthChckHedrRsps? = null,
  // 2. DB connection check
  var dbRowCnt: Long? = null
)
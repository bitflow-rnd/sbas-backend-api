package id.g8id.api.rsps

import org.sbas.dtos.SrvrPblcInfo
import org.sbas.dtos.SrvrSstmBsic
import org.sbas.responses.HlthChckHedrRsps
import org.sbas.utils.annotation.NoArg


@NoArg
data class HlthChckFullRsps(
  // 4. server outbound public info
  // Expensive Task (not $$, but time and trial limit)
  var srvrPlus: SrvrPblcInfo? = null
) {
  // 1. (common) request header extracted client info
  var clnt: HlthChckHedrRsps? = null
  // 2. DB connection check
  var dbRowCnt: Long? = null
  // 3. server internal basic info
  var srvr: SrvrSstmBsic? = null
}


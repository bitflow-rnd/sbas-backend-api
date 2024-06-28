package org.sbas.dtos

import org.sbas.utils.annotation.NoArg

/**
 *  * {"ip":"66.249.75.9","country":"United States","cc":"US"}
 */
@NoArg
data class SrvrPblcInfo(
  var srvrPblcIp: String? = null,
  var srvrCtryNm: String? = null,
  var srvrCtryIso: String? = null
)
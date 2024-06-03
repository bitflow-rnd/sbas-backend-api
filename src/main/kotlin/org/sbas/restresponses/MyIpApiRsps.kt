package org.sbas.restresponses

import org.sbas.utils.annotation.NoArg


/**
 * response e.g.
 * {"ip":"66.249.75.9","country":"United States","cc":"US"}
 */
@NoArg
data class MyIpApiRsps(
  val ip: String
  , val country: String
  , val cc: String
  , var err: String?
)
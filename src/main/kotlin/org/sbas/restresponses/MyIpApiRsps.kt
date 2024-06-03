package org.sbas.restresponses

import org.sbas.utils.annotation.NoArg


/**
 * response e.g.
 * {"ip":"66.249.75.9","country":"United States","cc":"US"}
 */
class MyIpApiRsps(
  val ip: String? = null
  , val country: String? = null
  , val cc: String? = null
  , var err: String? = null
)
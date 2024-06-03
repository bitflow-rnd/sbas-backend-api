package org.sbas.dtos

import org.sbas.utils.annotation.NoArg

@NoArg
class LangCtry(
  var lang: String? = null,
  var ctry: String? = null
)
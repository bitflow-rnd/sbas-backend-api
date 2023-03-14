package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.sbas.restresponses.EgenHsptMdcncApiResponse.HsptMdcncBody.HsptMdcncItems.EgenHsptlMdcncItem

@Schema(description = "E-Gen 병의원 목록 응답")
data class EgenHsptMdcncResponse (
    override var result: List<EgenHsptlMdcncItem>? = null
): AbstractResponse<List<EgenHsptlMdcncItem>?>()
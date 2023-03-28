package org.sbas.responses

import org.sbas.entities.info.InfoInst

class InfoInstResponse (
    override var result: List<InfoInst>? = null
    ): AbstractResponse<List<InfoInst>?>()
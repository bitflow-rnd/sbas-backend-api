package org.sbas.restresponses

import javax.xml.bind.annotation.*

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenResponse {

    @XmlElement
    var header: EgenHeader? = null

    @XmlElement
    var body: EgenBody? = null
}

@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenHeader {

    @XmlElement
    var resultCode: String? = null

    @XmlElement
    var resultMsg: String? = null
}

@XmlRootElement(name = "body")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenBody {
    @XmlElement(name = "items")
    var items: EgenItems? = null
}

@XmlRootElement(name = "items")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenItems {
    @XmlElement(name = "item")
    var item: List<EgenCodeMastItem>? = null
}

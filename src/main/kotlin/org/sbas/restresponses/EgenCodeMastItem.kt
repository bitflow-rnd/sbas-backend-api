package org.sbas.restresponses

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenCodeMastItem {

    @XmlElement
    var cmMid: String? = null

    @XmlElement
    var cmMnm: String? = null

    @XmlElement
    var cmSid: String? = null

    @XmlElement
    var cmSnm: String? = null

}

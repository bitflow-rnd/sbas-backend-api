package org.sbas.restresponses

import javax.xml.bind.annotation.*

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenCodeMastlApiResponse {

    @XmlElement
    var header: CodeMastHeader? = null

    @XmlElement
    var body: CodeMastBody? = null

    @XmlRootElement(name = "header")
    @XmlAccessorType(XmlAccessType.FIELD)
    class CodeMastHeader {
        @XmlElement
        var resultCode: String? = null

        @XmlElement
        var resultMsg: String? = null
    }

    @XmlRootElement(name = "body")
    @XmlAccessorType(XmlAccessType.FIELD)
    class CodeMastBody {
        @XmlElement
        var items: CodeMastItems? = null

        @XmlRootElement(name = "items")
        @XmlAccessorType(XmlAccessType.FIELD)
        class CodeMastItems {
            @XmlElement
            var item: List<EgenCodeMastItem>? = null

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

        }
    }

}



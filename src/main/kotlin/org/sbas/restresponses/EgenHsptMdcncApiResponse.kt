package org.sbas.restresponses

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
class EgenHsptMdcncApiResponse {

    @XmlElement
    var header: HsptMdcncHeader? = null

    @XmlElement
    var body: HsptMdcncBody? = null

    @XmlRootElement(name = "header")
    @XmlAccessorType(XmlAccessType.FIELD)
    class HsptMdcncHeader {
        @XmlElement
        var resultCode: String? = null

        @XmlElement
        var resultMsg: String? = null
    }

    @XmlRootElement(name = "body")
    @XmlAccessorType(XmlAccessType.FIELD)
    class HsptMdcncBody {
        @XmlElement
        var items: HsptMdcncItems? = null

        @XmlRootElement(name = "items")
        @XmlAccessorType(XmlAccessType.FIELD)
        class HsptMdcncItems {
            @XmlElement
            var item: List<EgenHsptlMdcncItem>? = null

            @XmlRootElement(name = "item")
            @XmlAccessorType(XmlAccessType.FIELD)
            class EgenHsptlMdcncItem {

                @XmlElement
                var dutyAddr: String? = null
                @XmlElement
                var dutyDiv: String? = null
                @XmlElement
                var dutyDivNam: String? = null
                @XmlElement
                var dutyEmcls: String? = null
                @XmlElement
                var dutyEmclsName: String? = null
                @XmlElement
                var dutyEryn: String? = null
                @XmlElement
                var dutyName: String? = null
                @XmlElement
                var dutyTel1: String? = null
                @XmlElement
                var dutyTime1c: String? = null
                @XmlElement
                var dutyTime1s: String? = null
                @XmlElement
                var dutyTime2c: String? = null
                @XmlElement
                var dutyTime2s: String? = null
                @XmlElement
                var dutyTime3c: String? = null
                @XmlElement
                var dutyTime3s: String? = null
                @XmlElement
                var dutyTime4c: String? = null
                @XmlElement
                var dutyTime4s: String? = null
                @XmlElement
                var dutyTime5c: String? = null
                @XmlElement
                var dutyTime5s: String? = null
                @XmlElement
                var hpid: String? = null
                @XmlElement
                var postCdn1: String? = null
                @XmlElement
                var postCdn2: String? = null
                @XmlElement
                var rnum: String? = null
                @XmlElement
                var wgs84Lat: String? = null
                @XmlElement
                var wgs84Lon: String? = null
            }

        }
    }

}

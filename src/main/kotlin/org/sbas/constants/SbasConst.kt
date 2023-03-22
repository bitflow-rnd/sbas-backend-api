package org.sbas.constants

object SbasConst {

    // Temp
    const val MSG_SEND_NO = "01088657020"

    val EGEN_GRP_CDS = arrayOf("Y000", "SIDO", "P004", "P001", "O000", "N000", "M000", "J004", "J002", "J000", "H030", "H020", "H000")

    /**
     * Naver Sens SMS meg send types
     */
    object MsgType {
        const val SMS = "SMS"
        const val MMS = "MMS"
    }
    object ResCode {
        const val SUCCESS = "00"
        const val FAIL = "01"
        const val FAIL_VALIDATION = "02"
    }

    object FileTypeCd {
        const val IMAGE = "FLTP0001"
        const val VIDEO = "FLTP0002"
    }

    /**
     * 공통코드그룹 ID
     */
    object CommCodeGrpCd {
        const val ORGANIZTN_TP = "ORG"
        const val PERMISSON = "PMG"
        const val PERMMISION_DTL = "DPM"
        const val PATIENT_TP = "PTT"
        const val SEVERITIES = "SVR"
        const val ASGNREJECT_RESN = "BAN"
        const val BEDASIGN_STAT = "BAS"
        const val DISCHARG_RESN = "DSC"
        const val HSPTINOUT_STAT = "IOS"
        const val MYORG_YN = "OGT"
        const val APPROVE_YN = "ARW"
        const val SIDO = "SDO"
        const val SIGUNGU = "SGG"
    }
}

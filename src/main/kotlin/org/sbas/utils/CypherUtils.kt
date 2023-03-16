package org.sbas.utils

import java.security.MessageDigest

/**
 * 암복호화 처리 도움 유틸
 */
class CypherUtils {

    companion object {
        fun crypto(inputStr:String):String{
            val sha=MessageDigest.getInstance("SHA-256")
            val hexa=sha.digest(inputStr.toByteArray())
            return hexa.fold("") { str, it -> str + "%02x".format(it) }
        }

    }
}
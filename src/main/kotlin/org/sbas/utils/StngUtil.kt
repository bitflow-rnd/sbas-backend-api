package org.sbas.utils

import io.seruco.encoding.base62.Base62
import java.nio.ByteBuffer
import kotlin.math.pow

class StngUtil {

  companion object {

    fun encodeToBase62String(rawString: String): String {
      return Base62.createInstance().encode(rawString.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
    }

    fun encodeToBase62String(byteArray: ByteArray): String {
      return Base62.createInstance().encode(byteArray).toString(Charsets.UTF_8)
    }

    fun encodeToBase62AsciiString(byteArray: ByteArray): String {
      return Base62.createInstance().encode(byteArray).toString(Charsets.US_ASCII)
    }

    fun decodeBase62String(base62String: String): String {
      return Base62.createInstance().decode(base62String.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
    }

    /**
     * https://kotlinlang.org/docs/numbers.html
     */
    fun getRandomBase62String(digit: Int): String {
      // digit:
      val nSqrt = 62.0.pow(digit.toDouble()).toInt()
      return encodeToBase62String(intToBytes((0..nSqrt).random())).trimStart('0')
    }

    fun getUniqueStringLeng15(): String {
      val todyNum62 = encodeToBase62AsciiString(longToBytes(TimeUtil.getTodayYyMMddssSSS().toLong()))
      val rndmNum62 = encodeToBase62String(intToBytes((0..(62*3 - 1)).random())).trimStart('0')
      return todyNum62 + rndmNum62
    }

    fun generateGrupId(): String {
      val todyNum62 = encodeToBase62AsciiString(longToBytes(TimeUtil.getTodayYyMMddssSSS().toLong()))
      val rndmNum62 = encodeToBase62String(intToBytes((0..(62*3 - 1)).random())).trimStart('0')
      return todyNum62 + rndmNum62
    }

    fun longToBytes(value: Long): ByteArray {
      val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
      buffer.putLong(value)
      return buffer.array()
    }

    fun intToBytes(value: Int): ByteArray {
      val buffer = ByteBuffer.allocate(Int.SIZE_BYTES)
      buffer.putInt(value)
      return buffer.array()
    }

    fun shortToBytes(value: Short): ByteArray {
      val buffer = ByteBuffer.allocate(Short.SIZE_BYTES)
      buffer.putShort(value)
      return buffer.array()
    }

    fun isMobile(platform: String?): Boolean {
      if (platform==null) { return false }
      val mobile = arrayOf("android", "iphone", "ipad")
      return mobile.contains(platform.lowercase())
    }

    fun getEsmtPlfmFromUserAgnt(userAgnt: String): String? {

      val userAgent = userAgnt.lowercase()
      var ret: String? = null
      if (userAgent.contains("android")) {
        ret = "android"
      } else if (userAgent.contains("windows")) {
        ret = "windows"
      } else if (userAgent.contains("os x")) {
        if (userAgent.contains("mobile")) {
          if (userAgent.contains("iphone")) {
            ret = "iphone"
          } else if (userAgent.contains("ipad")) {
            ret = "ipad"
          } else if (userAgent.contains("macintosh")) {
            ret = "ipad"
          }
        } else if (userAgent.contains("macintosh")) {
          ret = "macintosh"
        }
      } else if (userAgent.contains("linux")) {
        ret = "linux"
      }
      return ret
    }
    
  }


}
package org.sbas.utils

import org.sbas.dtos.LangCtry
import java.util.*


object LocaleUtil {

    private var map = mutableMapOf<String, String>()
    private var instance: LocaleUtil? = null

    fun init() {
        val locales = Locale.getAvailableLocales()
        for (locale in locales) {
            if ((locale.displayCountry != null) && ("" != locale.displayCountry)) {
                map[locale.language] = locale.country
            }
        }
    }

    fun getCounryCode(code: String): String {
        var locale: Locale = Locale.forLanguageTag(code)
        if ("" == locale.getCountry()) {
            locale = Locale(code, map.get(code))
        }
        return locale.getCountry()
    }

    fun getCounryName(code: String?): String {
        var locale: Locale = Locale.forLanguageTag(code)
        if ("" == locale.getCountry()) {
            locale = Locale(code, map.get(code))
        }

        return locale.getDisplayCountry()
    }

    fun getLanguage(code: String?): String {
        var locale: Locale = Locale.forLanguageTag(code)
        if ("" == locale.getCountry()) {
            locale = Locale(code, map.get(code))
        }

        return locale.getDisplayLanguage()
    }

    fun get(code: String?): String {
        var locale: Locale = Locale.forLanguageTag(code)
        if ("" == locale.getCountry()) {
            locale = Locale(code, map.get(code))
        }
        val sb = StringBuilder()
        sb.append("Language = ").append(locale.getDisplayLanguage())
        sb.append(", Country = ").append(locale.getDisplayCountry())
        sb.append(", Language (Country) = ").append(locale.getDisplayName())
        sb.append("\n")
        return sb.toString()
    }

    fun getEsmtCtryCrcyByAcptLang(acptLang: String): LangCtry? {
        val list = Locale.LanguageRange.parse(acptLang)
        val ret  = LangCtry()
        for (i in 0..< list.size) {
            try {
                val langRnge = list[i].range
                val lcal = Locale.forLanguageTag(langRnge)
                ret.ctry = lcal.country
                ret.lang = lcal.language
                return ret
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun getEsmtCtryAndCrcy(acptLang: String?, accsCtry: String?): MutableMap<String, String> {
        val ret = mutableMapOf<String, String>()
        if (acptLang!=null) {
            try {
                var inpt1 = Locale.LanguageRange.parse(acptLang)[0].toString()
                if (inpt1.length==2 || inpt1.indexOf("-")<0) {
                    val inpt2 = Locale.LanguageRange.parse(acptLang)[1].toString()
                    if (inpt2.length>2 || inpt2.indexOf("-")>0) {
                        inpt1 = inpt2
                    }
                }
                println("[HLTHCHCKDAO] estimate ctry/crcy with hedr Accept-Language " + acptLang + " / " + inpt1 + " / " + Locale.forLanguageTag(inpt1).country + " / " + Currency.getInstance(Locale.forLanguageTag(inpt1)).currencyCode)
                ret[Locale.forLanguageTag(inpt1).country] = Currency.getInstance(Locale.forLanguageTag(inpt1)).currencyCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (accsCtry!=null) {
            ret[accsCtry] = Currency.getInstance(Locale("", accsCtry)).currencyCode
        }
        return ret
    }

}
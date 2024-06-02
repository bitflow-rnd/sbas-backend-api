package org.sbas.constants

import jakarta.persistence.AttributeConverter
import org.sbas.constants.enums.SbasEnum
import org.sbas.constants.enums.UndrDsesCd

open class SbasEnumListConverter<T : SbasEnum>(private val enumClass: Class<T>) : AttributeConverter<List<T>, String> {

    override fun convertToDatabaseColumn(attribute: List<T>?): String? {
        if (attribute.isNullOrEmpty()) {
            return null
        }
        return attribute.joinToString(";")
    }

    override fun convertToEntityAttribute(dbData: String?): List<T>? {
        return dbData?.split(";")?.mapNotNull { code -> findEnumByCdNm(code) }
    }

    private fun findEnumByCdNm(code: String): T? {
        return enumClass.enumConstants.find { it.code == code }
    }
}

class UndrDsesCdConverter : SbasEnumListConverter<UndrDsesCd>(UndrDsesCd::class.java)


//class UndrDsesCdConverter : AttributeConverter<List<SbasEnum>, String> {
//
//    override fun convertToDatabaseColumn(attribute: List<SbasEnum>?): String? {
//        if (attribute.isNullOrEmpty()) {
//            return null
//        }
//        return attribute.joinToString(";")
//    }
//
//    override fun convertToEntityAttribute(dbData: String?): List<SbasEnum>? {
//        return dbData?.split(";")?.map { findEnumByCdNm(it) }
//    }
//
//    private fun findEnumByCdNm(code: String): UndrDsesCd {
//        return UndrDsesCd.valueOf(code)
//    }
//
//}
//
//class PtTypeCdConverter : AttributeConverter<List<SbasEnum>, String> {
//
//    override fun convertToDatabaseColumn(attribute: List<SbasEnum>?): String? {
//        if (attribute.isNullOrEmpty()) {
//            return null
//        }
//        return attribute.joinToString(";")
//    }
//
//    override fun convertToEntityAttribute(dbData: String?): List<SbasEnum>? {
//        return dbData?.split(";")?.map { findEnumByCdNm(it) }
//    }
//
//    private fun findEnumByCdNm(code: String): PtTypeCd {
//        return PtTypeCd.valueOf(code)
//    }
//
//}
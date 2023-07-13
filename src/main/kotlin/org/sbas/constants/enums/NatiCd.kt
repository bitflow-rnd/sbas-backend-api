package org.sbas.constants.enums

enum class NatiCd(val cdNm: String) {
    NATI0001("대한민국"),
    NATI0002("직접입력"),
    NATI0003("알수없음"),
    ;
}


//enum class CalculatorType(private val expression: Function<Long, Long>) {
//    CALC_A(Function { value: Long -> value }),
//    CALC_B(Function { value: Long -> value * 10 }),
//    CALC_C(Function { value: Long -> value * 3 }),
//    CALC_D(Function { 0L });
//
//    fun calculate(value: Long): Long {
//        return expression.apply(value)
//    }
//}



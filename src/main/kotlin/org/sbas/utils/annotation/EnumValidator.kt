package org.sbas.utils.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<ValidEnum, String?> {

    private lateinit var annotation: ValidEnum

    override fun initialize(constraintAnnotation: ValidEnum) {
        annotation = constraintAnnotation
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        val enumValues = this.annotation.enumClass.java.enumConstants
        val values = mutableListOf<String>()
        var res = 0

        if (value.isNullOrEmpty()) {
            return this.annotation.isNullable
        }

        if (!enumValues.isNullOrEmpty()) {
            if (value.contains(";")) {
                value.removeSuffix(";")
                values.addAll(value.split(";"))
                for (enumValue in enumValues) {
                    values.forEach {
                        if (it == enumValue.toString()) {
                            res++
                        }
                    }
                }
                return res == values.size
            }

            for (enumValue in enumValues) {
                if (value == enumValue.toString()) {
                    return true
                }
            }
        }

        return false
    }
}

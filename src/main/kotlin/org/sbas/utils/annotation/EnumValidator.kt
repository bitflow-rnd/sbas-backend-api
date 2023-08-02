package org.sbas.utils.annotation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<ValidEnum, String> {

    private lateinit var annotation: ValidEnum

    override fun initialize(constraintAnnotation: ValidEnum) {
        annotation = constraintAnnotation
    }

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        val enumValues = this.annotation.enumClass.java.enumConstants
        if (!enumValues.isNullOrEmpty()) {
            for (enumValue in enumValues) {
                if (value == enumValue.toString()) {
                    return true
                }
            }
        }
        return false
    }

}
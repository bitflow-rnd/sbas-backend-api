package org.sbas.handlers

import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse

/**
 * 파라미터 validation 실패 시 처리
 */
@Provider
class ConstraintViolationMapper : ExceptionMapper<ConstraintViolationException> {

    override fun toResponse(e: ConstraintViolationException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(CommonResponse(SbasConst.ResCode.FAIL_VALIDATION,
                null,
                e.constraintViolations.map {
                    PropertyValidationError(
                        it.propertyPath.toString(),
//                    it.messageTemplate,
                        it.message
                    )
                }))
            .build()

    }
}

class PropertyValidationError(
    val property: String,
//    val code: String,
    val message: String)
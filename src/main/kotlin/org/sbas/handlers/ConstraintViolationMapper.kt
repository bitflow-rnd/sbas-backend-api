package org.sbas.handlers

import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse
import javax.validation.ConstraintViolationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider


/**
 * 파라미터 validation 실패 시 처리
 */
//TODO 수정필요
@Provider
class ConstraintViolationMapper : ExceptionMapper<ConstraintViolationException> {

    override fun toResponse(e: ConstraintViolationException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(CommonResponse(SbasConst.ResCode.FAIL,
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
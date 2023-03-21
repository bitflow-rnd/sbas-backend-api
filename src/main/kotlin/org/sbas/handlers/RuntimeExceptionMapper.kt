package org.sbas.handlers

import org.sbas.response.CommonResponse
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider


/**
 * NotFoundException 발생 시 예외 응답 처리
 */
//TODO 수정필요
@Provider
class RuntimeExceptionMapper : ExceptionMapper<NotFoundException> {

    override fun toResponse(exception: NotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(CommonResponse(Response.Status.NOT_FOUND.statusCode, exception.message!!, null))
            .build()
    }
}
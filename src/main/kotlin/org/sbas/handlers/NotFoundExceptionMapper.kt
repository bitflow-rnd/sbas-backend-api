package org.sbas.handlers

import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse
import javax.persistence.RollbackException
import javax.ws.rs.BadRequestException
import javax.ws.rs.InternalServerErrorException
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
class NotFoundExceptionMapper : ExceptionMapper<NotFoundException> {
    override fun toResponse(exception: NotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class BadRequestExceptionMapper : ExceptionMapper<BadRequestException> {
    override fun toResponse(exception: BadRequestException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class ArrayIndexOutOfBoundsExceptionMapper : ExceptionMapper<ArrayIndexOutOfBoundsException> {
    override fun toResponse(exception: ArrayIndexOutOfBoundsException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class StringIndexOutOfBoundsExceptionMapper: ExceptionMapper<StringIndexOutOfBoundsException> {
    override fun toResponse(exception: StringIndexOutOfBoundsException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class EnumConstantNotPresentExceptionMapper: ExceptionMapper<EnumConstantNotPresentException> {
    override fun toResponse(exception: EnumConstantNotPresentException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class InternalServerErrorExceptionMapper: ExceptionMapper<InternalServerErrorException> {
    override fun toResponse(exception: InternalServerErrorException): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}

@Provider
class RollbackExceptionMapper: ExceptionMapper<RollbackException> {
    override fun toResponse(exception: RollbackException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message!!, null))
            .build()
    }
}
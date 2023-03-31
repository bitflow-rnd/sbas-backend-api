package org.sbas.handlers

import org.postgresql.util.PSQLException
import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse
import org.sbas.utils.CustomizedException
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
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "NotFoundException", null))
            .build()
    }
}

@Provider
class BadRequestExceptionMapper : ExceptionMapper<BadRequestException> {
    override fun toResponse(exception: BadRequestException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "BadRequestException", null))
            .build()
    }
}

@Provider
class ArrayIndexOutOfBoundsExceptionMapper : ExceptionMapper<ArrayIndexOutOfBoundsException> {
    override fun toResponse(exception: ArrayIndexOutOfBoundsException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "ArrayIndexOutOfBoundsException", null))
            .build()
    }
}

@Provider
class StringIndexOutOfBoundsExceptionMapper: ExceptionMapper<StringIndexOutOfBoundsException> {
    override fun toResponse(exception: StringIndexOutOfBoundsException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "StringIndexOutOfBoundsException", null))
            .build()
    }
}

@Provider
class EnumConstantNotPresentExceptionMapper: ExceptionMapper<EnumConstantNotPresentException> {
    override fun toResponse(exception: EnumConstantNotPresentException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "EnumConstantNotPresentException", null))
            .build()
    }
}

@Provider
class InternalServerErrorExceptionMapper: ExceptionMapper<InternalServerErrorException> {
    override fun toResponse(exception: InternalServerErrorException): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "InternalServerErrorException", null))
            .build()
    }
}

@Provider
class PSQLExceptionMapper: ExceptionMapper<PSQLException> {
    override fun toResponse(exception: PSQLException): Response {
        return Response.status(Response.Status.CONFLICT)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "PSQLException", null))
            .build()
    }
}

@Provider
class NullPointerExceptionMapper: ExceptionMapper<NullPointerException> {
    override fun toResponse(exception: NullPointerException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "NullPointerException", null))
            .build()
    }
}

@Provider
class CustomizedExceptionMapper: ExceptionMapper<CustomizedException> {
    override fun toResponse(exception: CustomizedException?): Response {
        return Response.status(exception!!.status)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message, null))
            .build()
    }
}
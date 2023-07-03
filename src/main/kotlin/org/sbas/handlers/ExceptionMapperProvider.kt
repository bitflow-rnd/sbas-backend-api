package org.sbas.handlers

import io.quarkus.arc.Priority
import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.ForbiddenException
import io.quarkus.security.UnauthorizedException
import org.postgresql.util.PSQLException
import org.sbas.constants.SbasConst
import org.sbas.responses.CommonResponse
import org.sbas.utils.CustomizedException
import java.sql.SQLException
import javax.persistence.PersistenceException
import javax.ws.rs.BadRequestException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.NotFoundException
import javax.ws.rs.Priorities
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider


/**
 * NotFoundException 발생 시 예외 응답 처리
 */
@Provider
class NotFoundExceptionMapper : ExceptionMapper<NotFoundException> {
    override fun toResponse(exception: NotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message, null))
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
            .entity(CommonResponse(SbasConst.ResCode.FAIL, exception.message, null))
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

@Provider
@Priority(Priorities.AUTHENTICATION)
class AuthenticationFailedExceptionMapper : ExceptionMapper<AuthenticationFailedException> {
    override fun toResponse(exception: AuthenticationFailedException): Response {
        return Response.status(401)
            .header("WWW-Authenticate", "Basic realm=\"Quarkus\"")
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "token 불일치", null))
            .build()
    }
}

@Provider
class UnauthorizedExceptionMapper : ExceptionMapper<UnauthorizedException> {
    override fun toResponse(exception: UnauthorizedException): Response {
        return Response.status(Response.Status.UNAUTHORIZED)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "로그인 후 이용해 주세요.", null))
            .build()
    }
}

@Provider
class ForbiddenExceptionMapper : ExceptionMapper<ForbiddenException> {
    override fun toResponse(exception: ForbiddenException): Response {
        return Response.status(Response.Status.FORBIDDEN)
            .type(MediaType.APPLICATION_JSON)
            .entity(CommonResponse(SbasConst.ResCode.FAIL, "권한이 없습니다.", null))
            .build()
    }
}

//@Provider
//class PersistenceExceptionMapper: ExceptionMapper<PersistenceException> {
//    override fun toResponse(exception: PersistenceException): Response {
//        val message = if (exception.cause!!.cause != null) {
//            exception.cause!!.cause!!.message
//        } else if (exception.cause != null) {
//            exception.cause!!.message
//        } else {
//            exception.message
//        }
//        return Response.status(Response.Status.CONFLICT)
//            .type(MediaType.APPLICATION_JSON)
//            .entity(CommonResponse(SbasConst.ResCode.FAIL, message,null))
//            .build()
//    }
//}
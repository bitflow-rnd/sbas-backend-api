package org.sbas.endpoints.private

import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.services.TalkService
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Tag(name = "대화방 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 대화방 등록 및 조회 등")
@RolesAllowed("USER")
@Path("v1/private/talk")
class PrivateTalkEndpoint {

    @Inject
    lateinit var talkService: TalkService

    @Inject
    lateinit var jwt: JsonWebToken

    @Operation(summary = "대화방 목록", description = "그룹 대화방, 1:1 대화방 목록 API")
    @GET
    @Path("my-chats")
    fun getMyChats(): Response {
        return Response.ok(talkService.getMyChats(jwt)).build()
    }

    @Operation(summary = "대화방 상세", description = "대화방 상세 API")
    @GET
    @Path("my-chat/{tkrmId}")
    fun getMyChat(@RestPath tkrmId: String): Response {
        return Response.ok(talkService.getMyChat(tkrmId)).build()
    }

}
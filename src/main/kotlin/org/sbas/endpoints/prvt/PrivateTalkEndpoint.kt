package org.sbas.endpoints.prvt

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestPath
import org.sbas.dtos.RegGroupTalkRoomDto
import org.sbas.dtos.RegTalkRoomDto
import org.sbas.services.TalkService

@Tag(name = "대화방 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 대화방 등록 및 조회 등")
//@RolesAllowed("USER")
@Path("v1/private/talk")
class PrivateTalkEndpoint {

    @Inject
    lateinit var talkService: TalkService

    @Inject
    lateinit var jwt: JsonWebToken

    @Operation(summary = "개인대화방 생성", description = "대화방 생성 API")
    @POST
    @Path("personal")
    fun regPersonalChatRoom(regRequest: RegTalkRoomDto): Response {
        return Response.ok(talkService.regPersonalChatRoom(regRequest)).build()
    }

    @Operation(summary = "단체대화방 생성", description = "단체대화방 생성 API")
    @POST
    @Path("group")
    fun regGroupChatRoom(regRequest: RegGroupTalkRoomDto): Response {
        return Response.ok(talkService.regGroupChatRoom(regRequest)).build()
    }

    @Operation(summary = "대화방 목록", description = "그룹 대화방, 1:1 대화방 목록 API")
    @GET
    @Path("my-chats")
    fun getMyChats(): Response {
        return Response.ok(talkService.getMyChats(jwt.name)).build()
    }

    @Operation(summary = "대화방 상세", description = "대화방 상세 API")
    @GET
    @Path("my-chat/{tkrmId}")
    fun getMyChat(@RestPath tkrmId: String): Response {
        return Response.ok(talkService.getMyChat(tkrmId)).build()
    }

    @GET
    @Path("all-chats")
    fun getAllChats(): Response {
        return Response.ok(talkService.getAllChats()).build()
    }
}
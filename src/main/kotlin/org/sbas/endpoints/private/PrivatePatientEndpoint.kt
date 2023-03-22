package org.sbas.endpoints.private

import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.entities.info.InfoPt
import org.sbas.response.CommonResponse
import org.sbas.response.StringResponse
import org.sbas.response.patient.EpidResult
import org.sbas.services.CommonService
import org.sbas.services.PatientService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

@Tag(name = "환자 관리(사용자 권한용)", description = "로그인 된 사용자(세부권한별 분기) - 환자 등록 및 조회 등")
@Path("v1/private/patient")
class PrivatePatientEndpoint {

    @Inject
    lateinit var log: Logger

    @Inject
    lateinit var patientService: PatientService

    @Inject
    lateinit var service1: CommonService

    @Operation(summary = "", description = "")
    @POST
    @Path("upldepidreport")
    fun upldepidreport(@RestForm param1: String, @RestForm param2: FileUpload):
            RestResponse<CommonResponse<EpidResult>?> {
        val res = patientService.uploadEpidReport(param2)
        return ResponseBuilder.ok(res).build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("delepidreport")
    fun delepidreport():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "환자기본정보 등록", description = "")
    @POST
    @Path("regbasicinfo")
    fun regbasicinfo(@RequestBody infoPt: InfoPt): RestResponse<StringResponse> {
        return try {
            val res = patientService.saveInfoPt(infoPt)
            ResponseBuilder.ok(res).build()
        } catch (e: Exception) {
            val res = StringResponse()
            res.code = "01"
            res.message = e.localizedMessage
            ResponseBuilder.serverError<StringResponse>()
                .entity(res)
                .type(MediaType.APPLICATION_JSON)
                .build()
        }
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("exist")
    fun exist():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("modinfo")
    fun modinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("basicinfo/{param}")
    fun basicinfo(@RestPath param: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("timeline/{param1}/{param2}")
    fun timeline(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("disesinfo/{param1}/{param2}")
    fun disesinfo(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @GET
    @Path("sevrinfo/{param1}/{param2}")
    fun sevrinfo(@RestPath param1: String, @RestPath param2: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("bioinfoanlys")
    fun bioinfoanlys():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regdisesinfo")
    fun regdisesinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regsevrinfo")
    fun regsevrinfo():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("regstrtpoint")
    fun regstrtpoint():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("sendmsg/{param}")
    fun sendmsg(@RestPath param: String):  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("search")
    fun search():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }

    @Operation(summary = "", description = "")
    @POST
    @Path("myorganiztn")
    fun myorganiztn():  RestResponse<StringResponse> {
        return ResponseBuilder.ok<StringResponse>().build()
    }
}
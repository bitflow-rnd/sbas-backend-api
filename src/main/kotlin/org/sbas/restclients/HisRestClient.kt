package org.sbas.restclients

import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restdtos.response.HisApiResponse

/**
 * HIS API를 처리하는 클라이언트
 */
@RegisterRestClient
interface FatimaHisRestClient {

  @POST
  @Path("get_svr_mntr_info_fnu/v1")
  fun getFatimaSvrtMntrInfo(body: HisRestClientRequest): HisApiResponse
}

@RegisterRestClient
interface KnuhHisRestClient {

  @POST
  @Path("get_svr_mntr_info_knuh/v1")
  fun getKnuhSvrtMntrInfo(body: HisRestClientRequest): HisApiResponse

  @POST
  @Path("get_svr_mntr_info_knuch/v1")
  fun getKnuchSvrtMntrInfo(body: HisRestClientRequest): HisApiResponse
}

@RegisterRestClient
interface DgmcHisRestClient {

  @POST
  @Path("get_svr_mntr_info_dmc/v1")
  fun getDgmcSvrtMntrInfo(body: HisRestClientRequest): HisApiResponse
}


data class HisRestClientRequest(
  val pid: String,
  val basedd: String? = null,
)

package org.sbas.restclients

import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.sbas.restresponses.HisApiResponse

/**
 * HIS API를 처리하는 클라이언트
 */
@RegisterRestClient(baseUri = "https://mcaredoctor.fatima.or.kr/qapiplus-dev/api/")
interface FatimaHisRestClient {

  @POST
  @Path("get_svr_mntr_info_fnu/v1")
  fun getFatimaSvrtMntrInfo(body: HisRestClientRequest): HisApiResponse
}

data class HisRestClientRequest(
  val pid: String,
  val basedd: String? = null,
)
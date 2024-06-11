package org.sbas.endpoints.pblc

import com.google.gson.Gson
import id.g8id.api.rsps.HlthChckDeftRsps
import id.g8id.api.rsps.HlthChckFullRsps
import id.g8id.api.rsps.HlthChckMinmRsps
import io.vertx.ext.web.RoutingContext
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.HttpHeaders
import org.jboss.logging.Logger
import org.sbas.constants.Cloudflare
import org.sbas.parameters.HlthChckHedrRqst
import org.sbas.responses.ComnRsps
import org.sbas.responses.HlthChckHedrRsps
import org.sbas.services.HlthChckSrvc

@Path("v1/public")
class HlthChckEndp {

  @Inject
  lateinit var log: Logger

  @Inject
  lateinit var context: RoutingContext

  @Inject
  lateinit var hlthChckSrvc: HlthChckSrvc


  @GET
  @Path("chck/head")
  fun getHlthChckHedr(
    rc: RoutingContext,
    @HeaderParam(HttpHeaders.USER_AGENT) userAgnt: String?,
    @HeaderParam(Cloudflare.CF_CONTINENT) ctnt: String?,
    @HeaderParam(Cloudflare.CF_COUNTRY) ctry: String?,
    @HeaderParam(Cloudflare.CF_REGION) regn: String?,
    @HeaderParam(Cloudflare.CF_CITY) city: String?,
    @HeaderParam(Cloudflare.CF_HOST) host: String?,
    @HeaderParam(Cloudflare.CF_TIMEZONE) tmzn: String?,
    @HeaderParam(Cloudflare.CF_REFERER) refr: String?,
    @HeaderParam(Cloudflare.CF_CONN_IP) connIp: String?,
    @HeaderParam(Cloudflare.X_REAL_IP) xRealIp: String?,
    @HeaderParam(Cloudflare.CF_PLATFORM) plfm: String?,
    @HeaderParam(Cloudflare.CF_LANG) lang: String?,
    @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) acptLang: String?
  ): ComnRsps<HlthChckHedrRsps> {
    val hedr = HlthChckHedrRqst(
      userAgnt, ctnt, ctry, regn, city, plfm, lang, host, connIp, tmzn, refr, xRealIp, acptLang
    )
    val ret = ComnRsps(hlthChckSrvc.getHlthChckHedr(rc, hedr))
    log.info("[HCHCK-HEDR] " + Gson().toJson(ret))
    return ret
  }

  /**
   * 2. Google Cloud Run에서 10초마다 한번 트리거 됨
   */
  @GET
  @Path("chck/minm")
  fun getHlthChckMinm(
    rc: RoutingContext,
    @HeaderParam(HttpHeaders.USER_AGENT) userAgnt: String?,
    @HeaderParam(Cloudflare.CF_CONTINENT) ctnt: String?,
    @HeaderParam(Cloudflare.CF_COUNTRY) ctry: String?,
    @HeaderParam(Cloudflare.CF_REGION) regn: String?,
    @HeaderParam(Cloudflare.CF_CITY) city: String?,
    @HeaderParam(Cloudflare.CF_HOST) host: String?,
    @HeaderParam(Cloudflare.CF_TIMEZONE) tmzn: String?,
    @HeaderParam(Cloudflare.CF_REFERER) refr: String?,
    @HeaderParam(Cloudflare.CF_CONN_IP) connIp: String?,
    @HeaderParam(Cloudflare.X_REAL_IP) xRealIp: String?,
    @HeaderParam(Cloudflare.CF_PLATFORM) plfm: String?,
    @HeaderParam(Cloudflare.CF_LANG) lang: String?,
    @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) acptLang: String?
  ): ComnRsps<HlthChckMinmRsps> {
    val hedr = HlthChckHedrRqst(
      userAgnt, ctnt, ctry, regn, city, plfm, lang, host, connIp, tmzn, refr, xRealIp, acptLang
    )
    val ret = ComnRsps(hlthChckSrvc.getHlthChckMinm(rc, hedr))
    log.info("[HCHCK-MINM] " + Gson().toJson(ret))
    return ret
  }

  /**
   * 2. 서버 내부까지 조사하는 헬스체커
   * 1보다 조금 범위 넓음
   */
  @GET
  @Path("chck/medm")
  fun getHlthChckDeft(
    rc: RoutingContext,
    @HeaderParam(HttpHeaders.USER_AGENT) userAgnt: String?,
    @HeaderParam(Cloudflare.CF_CONTINENT) ctnt: String?,
    @HeaderParam(Cloudflare.CF_COUNTRY) ctry: String?,
    @HeaderParam(Cloudflare.CF_REGION) regn: String?,
    @HeaderParam(Cloudflare.CF_CITY) city: String?,
    @HeaderParam(Cloudflare.CF_HOST) host: String?,
    @HeaderParam(Cloudflare.CF_TIMEZONE) tmzn: String?,
    @HeaderParam(Cloudflare.CF_REFERER) refr: String?,
    @HeaderParam(Cloudflare.CF_CONN_IP) connIp: String?,
    @HeaderParam(Cloudflare.X_REAL_IP) xRealIp: String?,
    @HeaderParam(Cloudflare.CF_PLATFORM) plfm: String?,
    @HeaderParam(Cloudflare.CF_LANG) lang: String?,
    @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) acptLang: String?
  ): ComnRsps<HlthChckDeftRsps> {

    val hedr = HlthChckHedrRqst(
      userAgnt, ctnt, ctry, regn, city, plfm, lang, host, connIp, tmzn, refr, xRealIp, acptLang
    )
    val ret = ComnRsps(hlthChckSrvc.getHlthChckDeft(rc, hedr))
    log.info("[HCHCK-MEDM] " + Gson().toJson(ret))
    return ret
  }

  @GET
  @Path("chck/maxm")
  fun getHlthChckFullWithSrvrSstm(
    rc: RoutingContext,
    @HeaderParam(HttpHeaders.USER_AGENT) userAgnt: String?,
    @HeaderParam(Cloudflare.CF_CONTINENT) ctnt: String?,
    @HeaderParam(Cloudflare.CF_COUNTRY) ctry: String?,
    @HeaderParam(Cloudflare.CF_REGION) regn: String?,
    @HeaderParam(Cloudflare.CF_CITY) city: String?,
    @HeaderParam(Cloudflare.CF_HOST) host: String?,
    @HeaderParam(Cloudflare.CF_TIMEZONE) tmzn: String?,
    @HeaderParam(Cloudflare.CF_REFERER) refr: String?,
    @HeaderParam(Cloudflare.CF_CONN_IP) connIp: String?,
    @HeaderParam(Cloudflare.X_REAL_IP) xRealIp: String?,
    @HeaderParam(Cloudflare.CF_PLATFORM) plfm: String?,
    @HeaderParam(Cloudflare.CF_LANG) lang: String?,
    @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) acptLang: String?
  ): ComnRsps<HlthChckFullRsps> {
    val hedr = HlthChckHedrRqst(
      userAgnt, ctnt, ctry, regn, city, plfm, lang, host, connIp, tmzn, refr, xRealIp, acptLang
    )

    val ret = ComnRsps(hlthChckSrvc.getHlthChckFullWithSrvrSstm(rc, hedr))
    log.info("[HCHCK-MAXM] " + Gson().toJson(ret))
    return ret
  }

}

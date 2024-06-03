package org.sbas.repositories

import com.google.gson.Gson
import io.quarkus.runtime.LaunchMode
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.dtos.SrvrPblcInfo
import org.sbas.dtos.SrvrSstmBsic
import org.sbas.parameters.HlthChckHedrRqst
import org.sbas.responses.HlthChckHedrRsps
import org.sbas.restclients.MyIpDao
import org.sbas.restresponses.MyIpApiRsps
import org.sbas.utils.LocaleUtil
import org.sbas.utils.StngUtil
import java.net.InetAddress
import java.util.*


@ApplicationScoped
class HlthChckDao {

  @Inject
  lateinit var log: Logger

  @RestClient
  lateinit var myIpDao: MyIpDao

  /**
   * request.(remote/local)Address
   *  => methods: host: String, hostName: String, hostAddress: String, path: String, port: Int
   */
  fun getSstmInfo(rc: RoutingContext): SrvrSstmBsic {
    val ret = SrvrSstmBsic()
    ret.srvrLnchProf = LaunchMode.current().defaultProfile // dev, prod, test...
    ret.srvrOsNm     = System.getProperty("os.name")       // Linux, Windows
    ret.srvrInnrIp   = InetAddress.getLocalHost().toString()
    ret.srvrRmotIp   = rc.request().remoteAddress().hostAddress()
    ret.srvrLcalIp   = rc.request().localAddress().hostAddress()
    return ret
  }
  @Throws(Exception::class)
  fun chckDbCntOrThrwErr(): Long? {
//    val cnt = CodeMast.count()
//    log.debug("[DBCONN] OK $cnt")
    return 1
  }

  /**
   * Expensive Task (not $$, but time and trial limit)
   */
  fun getSrvrPblcInfo(): SrvrPblcInfo? {
    val res: MyIpApiRsps = Gson().fromJson(myIpDao.getMyIp(), MyIpApiRsps::class.java)
    if (res.err.isNullOrBlank()) {
      return SrvrPblcInfo(res.ip, res.country, res.cc)
    } else {
      return null
    }
  }

  /**
   * CloudFlare를 통과하지 않아, 요청자 정보가 부족할 때 request header를 이용해서
   * 정보를 조금 얻어냄
   */
  private fun updtHedrCtryLangPlfm(hedr: HlthChckHedrRqst) {
    if (!hedr.userAgnt.isNullOrBlank()) {
      if (hedr.plfm == null) {
        hedr.plfm = StngUtil.getEsmtPlfmFromUserAgnt(hedr.userAgnt!!)
      } else {
        hedr.plfm = hedr.plfm!!.lowercase()
      }
      if ((hedr.lang == null || hedr.ctry == null) && !hedr.acptLang.isNullOrBlank()) {
        val ret = LocaleUtil.getEsmtCtryCrcyByAcptLang(hedr.acptLang!!)
        if (ret!=null) {
          if (hedr.lang.isNullOrBlank()) {
            hedr.lang = ret.lang
          }
          if (hedr.ctry.isNullOrBlank()) {
            hedr.brwsCtry = ret.ctry
          }
        }
      }
    }
  }

  /**
   * If localhost =>
   * [REMOTEIP] host/name/addr/path/port - 0:0:0:0:0:0:0:1)/null/0:0:0:0:0:0:0:1/null/2395 <- 여기 맨마지막 포트 바뀜 + accptLang?
   * [LOCALIP]  host/name/addr/path/port - 0:0:0:0:0:0:0:1)/null/0:0:0:0:0:0:0:1/null/8080
   */
  fun getFullClntInfoFromHedr(hedr: HlthChckHedrRqst, rc: RoutingContext): HlthChckHedrRsps {
    val srvrRmotIp = rc.request().remoteAddress()
    val srvrLcalIp = rc.request().localAddress()
    if (hedr.connIp.isNullOrBlank()) {
      if (hedr.xRealIp.isNullOrBlank()) {
        hedr.clntIp = srvrRmotIp.hostAddress()
      } else {
        hedr.clntIp = hedr.xRealIp
      }
    } else {
      hedr.clntIp = hedr.connIp
    }
    log.info("[RMOTE-IP] host/name/addr/path/port - ${srvrRmotIp.host()}/${srvrRmotIp.hostName()}/${srvrRmotIp.hostAddress()}/${srvrRmotIp.path()}/${srvrRmotIp.port()}")
    log.info("[LOCAL-IP] host/name/addr/path/port - ${srvrLcalIp.host()}/${srvrLcalIp.hostName()}/${srvrLcalIp.hostAddress()}/${srvrLcalIp.path()}/${srvrLcalIp.port()}")
    updtHedrCtryLangPlfm(hedr) // 리턴없이 업데이트 된 건가?
    return HlthChckHedrRsps(hedr)
  }

}
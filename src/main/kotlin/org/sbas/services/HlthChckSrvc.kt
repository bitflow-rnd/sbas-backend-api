package org.sbas.services

import id.g8id.api.rsps.HlthChckDeftRsps
import id.g8id.api.rsps.HlthChckFullRsps
import id.g8id.api.rsps.HlthChckMinmRsps
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jboss.logging.Logger
import org.sbas.parameters.HlthChckHedrRqst
import org.sbas.repositories.HlthChckDao
import org.sbas.responses.HlthChckHedrRsps


@ApplicationScoped
class HlthChckSrvc {

  @Inject
  lateinit var log: Logger

  @Inject
  lateinit var hlthChckDao: HlthChckDao

  fun updtFngrPrnt(rc: RoutingContext, hedr: HlthChckHedrRqst, rfp: String): String {
    val ret = getHlthChckHedr(rc, hedr)
    var ip = ret.rqstClntIp
    if (ip == null || ip == "0:0:0:0:0:0:0:1") {
      ip = rc.request().remoteAddress().port().toString()
      // ip = ip.replace(".", "9").replace(":", "8")
    }
    log.info("[USER] fp-in/fp-out: $rfp/$rfp$ip - hedr: $hedr")
    return (rfp + ip)
  }

  fun getHlthChckHedr(rc: RoutingContext, hedr: HlthChckHedrRqst): HlthChckHedrRsps {
    return hlthChckDao.getFullClntInfoFromHedr(hedr, rc)
  }

  fun getHlthChckMinm(rc: RoutingContext, hedr: HlthChckHedrRqst): HlthChckMinmRsps {
    val ret = HlthChckMinmRsps()
    ret.clnt = hlthChckDao.getFullClntInfoFromHedr(hedr, rc)
    ret.dbRowCnt = hlthChckDao.chckDbCntOrThrwErr()
    return ret
  }

  /**
   * Todo
   */
  fun getHlthChckDeft(rc: RoutingContext, hedr: HlthChckHedrRqst): HlthChckDeftRsps {
    val ret = HlthChckDeftRsps()
    ret.clnt = hlthChckDao.getFullClntInfoFromHedr(hedr, rc)
    ret.dbRowCnt = hlthChckDao.chckDbCntOrThrwErr()
    ret.srvr = hlthChckDao.getSstmInfo(rc)
    return ret
  }

  /**
   * Todo
   */
  fun getHlthChckFullWithSrvrSstm(rc: RoutingContext, hedr: HlthChckHedrRqst): HlthChckFullRsps? {
    val ret = HlthChckFullRsps()
    ret.clnt = hlthChckDao.getFullClntInfoFromHedr(hedr, rc)
    ret.dbRowCnt = hlthChckDao.chckDbCntOrThrwErr()
    ret.srvr = hlthChckDao.getSstmInfo(rc)
    // Expensive Task (not $$, but time and trial limit)
    ret.srvrPlus = hlthChckDao.getSrvrPblcInfo()
    return ret
  }

}
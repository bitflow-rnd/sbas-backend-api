package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoPt
import org.sbas.entities.talk.TalkMsg
import org.sbas.entities.talk.TalkUser
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TalkUserRepository : PanacheRepositoryBase<TalkUser, String> {

    fun findChatsByUserId(userId: String) = find("select tu.id.tkrmId from TalkUser tu where tu.id.userId = '$userId'").list()

}

@ApplicationScoped
class TalkMsgRepository : PanacheRepositoryBase<TalkMsg, String> {

    fun findChatDetail(tkrmId: String) = find("select tm from TalkMsg tm where tm.id.tkrmId = '$tkrmId' order by tm.id.msgSeq").list()

}
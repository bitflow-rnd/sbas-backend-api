package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.dtos.info.NoticeListReq
import org.sbas.entities.info.InfoNotice
import org.sbas.entities.info.NoticeReadStatus
import org.sbas.entities.info.NoticeReadStatusId
import org.sbas.responses.notice.NoticeListResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class NoticeRepository : PanacheRepositoryBase<InfoNotice, String> {

    @Inject
    private lateinit var noticeReadStatusRepository: NoticeReadStatusRepository
    fun findAllNoticeList(noticeListReq: NoticeListReq): List<NoticeListResponse> {
        val page = noticeListReq.page ?: 1
        val size = noticeListReq.size ?: 10
        val findList = find("order by start_notice_tm").page(page - 1, size).list()

        val resultList: MutableList<NoticeListResponse> = emptyList<NoticeListResponse>().toMutableList()

        findList.forEach {
            val readStatusId: NoticeReadStatusId = NoticeReadStatusId(userId = noticeListReq.userId, noticeId = it.noticeId)
            val findReadStatus = noticeReadStatusRepository.findById(readStatusId)
            resultList.add(
                NoticeListResponse(
                    noticeId = it.noticeId,
                    title = it.title,
                    content = it.content,
                    noticeType = it.noticeType,
                    startNoticeDt = it.startNoticeDt,
                    isRead = findReadStatus != null
                )
            )
        }

        return resultList
    }
}

@ApplicationScoped
class NoticeReadStatusRepository : PanacheRepositoryBase<NoticeReadStatus, NoticeReadStatusId> {


}
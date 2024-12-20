package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.jboss.logging.Logger
import org.sbas.dtos.info.NoticeListReq
import org.sbas.entities.info.InfoNotice
import org.sbas.entities.info.NoticeReadStatus
import org.sbas.entities.info.NoticeReadStatusId
import org.sbas.responses.notice.NoticeListResponse
import org.sbas.utils.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class NoticeRepository : PanacheRepositoryBase<InfoNotice, String> {

    @Inject
    private lateinit var noticeReadStatusRepository: NoticeReadStatusRepository

    @Inject
    lateinit var log: Logger

    fun findAllNoticeList(noticeListReq: NoticeListReq): List<NoticeListResponse> {
        val page = noticeListReq.page ?: 1
        val size = noticeListReq.size ?: 10
        val findList = find("order by startNoticeTm").page(page - 1, size).list()

        val today = StringUtils.getYyyyMmDd()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val todayDt = LocalDate.parse(today, formatter)

        val periodDate = when (noticeListReq.searchPeriod) {
            "1M" -> todayDt.minusMonths(1)
            "3M" -> todayDt.minusMonths(3)
            "1Y" -> todayDt.minusYears(1)
            else -> todayDt.minusMonths(1) // default 1달
        }

        val filterList = findList.filter {
            val isActiveMatch = it.isActive == noticeListReq.isActive || noticeListReq.isActive == null

            val startNoticeDt = LocalDate.parse(it.startNoticeDt, formatter)
            val isAfterPeriodDate = startNoticeDt.isAfter(periodDate)

            isActiveMatch && isAfterPeriodDate
        }

        val resultList: MutableList<NoticeListResponse> = emptyList<NoticeListResponse>().toMutableList()

        filterList.forEach {
            val readStatusId: NoticeReadStatusId = NoticeReadStatusId(userId = noticeListReq.userId, noticeId = it.noticeId)
            val findReadStatus = noticeReadStatusRepository.findById(readStatusId)
            resultList.add(
                it.toListResponse(findReadStatus != null, it.attcGrpId != null)
            )
        }

        return resultList
    }
}

@ApplicationScoped
class NoticeReadStatusRepository : PanacheRepositoryBase<NoticeReadStatus, NoticeReadStatusId> {


}
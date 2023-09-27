package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.InfoNotice
import org.sbas.parameters.PageRequest
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NoticeRepository : PanacheRepositoryBase<InfoNotice, String>{
    fun findAllNoticeList(pageRequest: PageRequest): List<InfoNotice>{
        val page = pageRequest.page ?: 1
        val size = pageRequest.size ?: 10
        return find("order by start_notice_tm").page(page-1, size).list()
    }
}
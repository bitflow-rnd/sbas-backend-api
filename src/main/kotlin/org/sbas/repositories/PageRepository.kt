package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.info.SavePage
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class PageRepository : PanacheRepositoryBase<SavePage, String> {

    @Transactional
    fun saveCurrentPage(id: String, page: Int) {
        val findPage = findById(id)
        findPage?.page = page
    }
}
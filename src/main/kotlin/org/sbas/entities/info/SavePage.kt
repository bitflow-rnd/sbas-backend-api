package org.sbas.entities.info

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "save_page")
class SavePage (
    @Id
    @Column(name = "info_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "page")
    var page: Int? = null
)
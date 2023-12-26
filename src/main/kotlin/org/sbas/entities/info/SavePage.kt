package org.sbas.entities.info

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "save_page")
class SavePage (
    @Id
    @Column(name = "info_id", nullable = false, length = 10)
    var id: String? = null,

    @Column(name = "page")
    var page: Int? = null
)
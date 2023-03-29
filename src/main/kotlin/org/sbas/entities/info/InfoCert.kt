package org.sbas.entities.info

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_cert")
class InfoCert (

    @Id
    @Column(name = "phone_no", nullable = false, length = 15)
    var phoneNo: String,

    @Column(name = "cert_no", nullable = false, length = 100)
    var certNo: String,

    @Column(name = "created_dttm", nullable = false)
    var createdDttm: Instant,

    @Column(name = "expires_dttm", nullable = false)
    var expiresDttm: Instant,

)
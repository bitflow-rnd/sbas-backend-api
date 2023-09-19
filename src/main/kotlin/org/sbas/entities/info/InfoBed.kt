package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serial
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info_bed")
class InfoBed(
    @Id
    @Column(name = "hosp_id")
    var hospId: String,

    @Id
    @Column(name = "hpid")
    var hpId: String,

    @Column(name = "gnbd_icu")
    var gnbdIcu: Int? = null,

    @Column(name = "npib_icu")
    var npidIcu: Int? = null,

    @Column(name = "gnbd_svrt")
    var gnbdSvrt: Int? = null,

    @Column(name = "gnbd_smsv")
    var gnbdSmsv: Int? = null,

    @Column(name = "gnbd_modr")
    var gnbdModr: Int? = null,

    @Column(name = "coht_bed")
    var cohtBed: Int? = null,

    @Column(name = "npib_svrt")
    var npibSvrt: Int? = null,

    @Column(name = "npib_smsv")
    var npibSmsv: Int? = null,

    @Column(name = "npib_modr")
    var npibModr: Int? = null,

    @Column(name = "npib_lbr_inft")
    var npibLbrInft: Int? = null,

    @Column(name = "ventilator")
    var ventilator: String? = null,

    @Column(name = "ventilator_preemie")
    var ventilatorPreemie: String? = null,

    @Column(name = "incubator")
    var incubator: String? = null,

    @Column(name = "ecmo")
    var ecmo: String? = null,

    @Column(name = "high_pressure_oxygen")
    var highPressureOxygen: String? = null,

    @Column(name = "ct")
    var ct: String? = null,

    @Column(name = "mri")
    var mri: String? = null,

    @Column(name = "blood_vessel_imaging")
    var bloodVesselImaging: String? = null,

    @Column(name = "body_temperature_control")
    var bodyTemperatureControl: String? = null,

) : CommonEntity(), Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 4876653874697519726L
    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is InfoBed) return false
//
//        if (hospId != other.hospId) return false
//        if (hpId != other.hpId) return false
//        if (gnbdIcu != other.gnbdIcu) return false
//        if (npidIcu != other.npidIcu) return false
//        if (gnbdSvrt != other.gnbdSvrt) return false
//        if (gnbdSmsv != other.gnbdSmsv) return false
//        if (gnbdModr != other.gnbdModr) return false
//        if (cohtBed != other.cohtBed) return false
//        if (npibSvrt != other.npibSvrt) return false
//        if (npibSmsv != other.npibSmsv) return false
//        if (npibModr != other.npibModr) return false
//        return npibLbrInft == other.npibLbrInft
//    }
//
//    override fun hashCode(): Int {
//        var result = hospId.hashCode()
//        result = 31 * result + hpId.hashCode()
//        result = 31 * result + (gnbdIcu ?: 0)
//        result = 31 * result + (npidIcu ?: 0)
//        result = 31 * result + (gnbdSvrt ?: 0)
//        result = 31 * result + (gnbdSmsv ?: 0)
//        result = 31 * result + (gnbdModr ?: 0)
//        result = 31 * result + (cohtBed ?: 0)
//        result = 31 * result + (npibSvrt ?: 0)
//        result = 31 * result + (npibSmsv ?: 0)
//        result = 31 * result + (npibModr ?: 0)
//        result = 31 * result + (npibLbrInft ?: 0)
//        return result
//    }
}
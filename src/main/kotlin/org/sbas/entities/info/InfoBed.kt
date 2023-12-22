package org.sbas.entities.info

import org.sbas.entities.CommonEntity
import java.io.Serial
import java.io.Serializable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

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
    var gnbdIcu: Int? = null,  // hv22 - 감염병 전담병상 중환자실

    @Column(name = "npib_icu")
    var npidIcu: Int? = null, // hv23 - 감염병 전담병상 중환자실 내 음압격리병상

    @Column(name = "gnbd_svrt")
    var gnbdSvrt: Int? = null, // hv24 - [감염] 중증 병상

    @Column(name = "gnbd_smsv")
    var gnbdSmsv: Int? = null, // hv25 - [감염] 준-중증 병상

    @Column(name = "gnbd_modr")
    var gnbdModr: Int? = null, // hv26 - [감염] 중등증 병상

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
    var ventilator: String? = null, // 인공호흡기

    @Column(name = "ventilator_preemie")
    var ventilatorPreemie: String? = null, // 인공호흡기 조산아

    @Column(name = "incubator")
    var incubator: String? = null, // 인큐베이터

    @Column(name = "ecmo")
    var ecmo: String? = null, // ECMO

    @Column(name = "high_pressure_oxygen")
    var highPressureOxygen: String? = null, // 고압산소치료기

    @Column(name = "ct")
    var ct: String? = null, // CT

    @Column(name = "mri")
    var mri: String? = null, // MRI

    @Column(name = "blood_vessel_imaging")
    var bloodVesselImaging: String? = null, // 혈관촬영기

    @Column(name = "body_temperature_control")
    var bodyTemperatureControl: String? = null, // 중심체온조절유도기

    @Column
    var emrgncyNrmlBed: Int?, // hvec - 일반(응급실일반병상)

    @Column
    var ngtvIsltnChild: Int?, // hv15 - 소아 음압격리

    @Column
    var nrmlIsltnChild: Int?, // hv16 - 소아 일반격리

    @Column
    var nrmlChildBed: Int?, // hv28 - 소아

    @Column
    var emrgncyNrmlIsltnBed: Int?, // hv30 - 응급실 일반 격리 병상

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
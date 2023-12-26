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
    var cohtBed: Int? = null, // 코호트 격리구역 가용 병상수

    @Column(name = "npib_svrt")
    var npibSvrt: Int? = null, // 가용 음압격리 병상수 (중증)

    @Column(name = "npib_smsv")
    var npibSmsv: Int? = null, // 가용 음압격리 병상수 (준중증)

    @Column(name = "npib_modr")
    var npibModr: Int? = null, // 가용 음압격리 병상수 (중등증)

    @Column(name = "npib_lbr_inft")
    var npibLbrInft: Int? = null, // 가용 음압격리 병상수 (산모 및 신생아)

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

    @Column(name = "emrgncy_ngtv_isltn_bed")
    var emrgncyNgtvIsltnBed: Int?, // hv29 - 응급실 음압 격리 병상

    @Column(name = "isltn_med_area_ngtv_isltn_bed")
    var isltnMedAreaNgtvIsltnBed: Int?, // hv13 - 격리 진료 구역 음압 격리 병상

    @Column(name = "isltn_med_area_nrml_isltn_bed")
    var isltnMedAreaNrmlIsltnBed: Int?, // hv14 - 격리 진료 구역 일반 격리 병상

) : CommonEntity(), Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 4876653874697519726L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InfoBed) return false

        if (hospId != other.hospId) return false
        if (hpId != other.hpId) return false
        if (gnbdIcu != other.gnbdIcu) return false
        if (npidIcu != other.npidIcu) return false
        if (gnbdSvrt != other.gnbdSvrt) return false
        if (gnbdSmsv != other.gnbdSmsv) return false
        if (gnbdModr != other.gnbdModr) return false
        if (cohtBed != other.cohtBed) return false
        if (npibSvrt != other.npibSvrt) return false
        if (npibSmsv != other.npibSmsv) return false
        if (npibModr != other.npibModr) return false
        if (npibLbrInft != other.npibLbrInft) return false
        if (ventilator != other.ventilator) return false
        if (ventilatorPreemie != other.ventilatorPreemie) return false
        if (incubator != other.incubator) return false
        if (ecmo != other.ecmo) return false
        if (highPressureOxygen != other.highPressureOxygen) return false
        if (ct != other.ct) return false
        if (mri != other.mri) return false
        if (bloodVesselImaging != other.bloodVesselImaging) return false
        if (bodyTemperatureControl != other.bodyTemperatureControl) return false
        if (emrgncyNrmlBed != other.emrgncyNrmlBed) return false
        if (ngtvIsltnChild != other.ngtvIsltnChild) return false
        if (nrmlIsltnChild != other.nrmlIsltnChild) return false
        if (nrmlChildBed != other.nrmlChildBed) return false
        if (emrgncyNrmlIsltnBed != other.emrgncyNrmlIsltnBed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hospId.hashCode()
        result = 31 * result + hpId.hashCode()
        result = 31 * result + (gnbdIcu ?: 0)
        result = 31 * result + (npidIcu ?: 0)
        result = 31 * result + (gnbdSvrt ?: 0)
        result = 31 * result + (gnbdSmsv ?: 0)
        result = 31 * result + (gnbdModr ?: 0)
        result = 31 * result + (cohtBed ?: 0)
        result = 31 * result + (npibSvrt ?: 0)
        result = 31 * result + (npibSmsv ?: 0)
        result = 31 * result + (npibModr ?: 0)
        result = 31 * result + (npibLbrInft ?: 0)
        result = 31 * result + (ventilator?.hashCode() ?: 0)
        result = 31 * result + (ventilatorPreemie?.hashCode() ?: 0)
        result = 31 * result + (incubator?.hashCode() ?: 0)
        result = 31 * result + (ecmo?.hashCode() ?: 0)
        result = 31 * result + (highPressureOxygen?.hashCode() ?: 0)
        result = 31 * result + (ct?.hashCode() ?: 0)
        result = 31 * result + (mri?.hashCode() ?: 0)
        result = 31 * result + (bloodVesselImaging?.hashCode() ?: 0)
        result = 31 * result + (bodyTemperatureControl?.hashCode() ?: 0)
        result = 31 * result + (emrgncyNrmlBed ?: 0)
        result = 31 * result + (ngtvIsltnChild ?: 0)
        result = 31 * result + (nrmlIsltnChild ?: 0)
        result = 31 * result + (nrmlChildBed ?: 0)
        result = 31 * result + (emrgncyNrmlIsltnBed ?: 0)
        return result
    }

}
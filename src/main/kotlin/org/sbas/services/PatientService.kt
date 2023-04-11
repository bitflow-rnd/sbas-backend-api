package org.sbas.services

import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.sbas.constants.SbasConst
import org.sbas.dtos.InfoPtDto
import org.sbas.dtos.NewsScoreParam
import org.sbas.dtos.toEntity
import org.sbas.entities.base.BaseAttc
import org.sbas.entities.info.InfoPt
import org.sbas.handlers.FileHandler
import org.sbas.handlers.NaverApiHandler
import org.sbas.parameters.SearchParameters
import org.sbas.repositories.BaseAttcRepository
import org.sbas.repositories.InfoInstRepository
import org.sbas.repositories.InfoPtRepository
import org.sbas.repositories.InfoUserRepository
import org.sbas.responses.CommonResponse
import org.sbas.responses.patient.EpidResult
import org.sbas.utils.StringUtils
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException


/**
 * 환자정보를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class PatientService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var infoPtRepository: InfoPtRepository

    @Inject
    private lateinit var infoUserRepository: InfoUserRepository

    @Inject
    private lateinit var infoInstRepository: InfoInstRepository

    @Inject
    private lateinit var fileHandler: FileHandler

    @Inject
    private lateinit var naverApiHandler: NaverApiHandler
    
    @Inject
    private lateinit var baseAttcRepository: BaseAttcRepository

    @Inject
    private lateinit var jwt: JsonWebToken

    @Transactional
    fun saveInfoPt(infoPtDto: InfoPtDto): CommonResponse<String?> {
        val infoPt = infoPtDto.toEntity()
        infoPtRepository.persist(infoPt)
        return CommonResponse(infoPt.id)
    }

    @Transactional
    fun check(infoPtDto: InfoPtDto): CommonResponse<String> {
        val findInfoPt = infoPtRepository.findByPtNmAndRrno(
            ptNm = infoPtDto.ptNm,
            rrno1 = infoPtDto.rrno1,
            rrno2 = infoPtDto.rrno2,
        )

        if (findInfoPt != null) { // 등록된 환자 존재
            return CommonResponse("등록된 환자가 존재합니다.")
        }
        return CommonResponse("등록된 환자가 존재하지 않습니다.")
    }

    fun calculateNewsScore(param: NewsScoreParam): Int {
        val list = mutableListOf<Int>()
        when {
            param.breath <= 8 -> list.add(0, 3)
            param.breath in 9..11 -> list.add(0, 1)
            param.breath in 12..20 -> list.add(0, 0)
            param.breath in 21..24 -> list.add(0, 2)
            else -> list.add(0, 3)
        }
        when {
            param.spo2 <= 91 -> list.add(1, 3)
            param.spo2 in 92..93 -> list.add(1, 2)
            param.spo2 in 94..95 -> list.add(1, 1)
            else -> list.add(1, 0)
        }
        when (param.o2Apply) {
            "Oxygen" -> list.add(2, 2)
            "Air" -> list.add(2, 0)
            else -> list.add(2, 0)
        }
        when {
            param.sbp <= 90 -> list.add(3,3)
            param.sbp in 91..100 -> list.add(3,2)
            param.sbp in 101..110 -> list.add(3,1)
            param.sbp in 111..219 -> list.add(3,0)
            else -> list.add(3,3)
        }
        when {
            param.pulse <= 40 -> list.add(4, 3)
            param.pulse in 41..50 -> list.add(4, 1)
            param.pulse in 51..90 -> list.add(4, 0)
            param.pulse in 91..110 -> list.add(4, 1)
            param.pulse in 111..130 -> list.add(4, 2)
            else -> list.add(4, 3)
        }
        when (param.avpu) {
            "Alert" -> list.add(5, 0)
            "CVPU" -> list.add(5, 3)
            else -> list.add(5, 0)
        }
        when {
            param.bdTemp <= 35.0 -> list.add(6, 3)
            param.bdTemp in 35.1..36.0 -> list.add(6, 1)
            param.bdTemp in 36.1..38.0 -> list.add(6, 0)
            param.bdTemp in 38.1..39.0 -> list.add(6, 1)
            param.bdTemp >= 39.1 -> list.add(6, 2)
        }
        return list.sum()
    }

    @Transactional
    fun uploadEpidReport(param: FileUpload): CommonResponse<EpidResult>? {
        val fileInfo = fileHandler.createPublicFile(param)
        if (fileInfo != null) {
            // Naver Clova OCR call
            val res = naverApiHandler.recognizeImage(fileInfo.uriPath, fileInfo.filename)
            log.debug("texts are $res")
            // Then move from public to private
            fileHandler.moveFilePublicToPrivate(fileInfo.localPath, fileInfo.filename)
            val item = BaseAttc(
                attcDt = StringUtils.getYyyyMmDd(),
                attcTm = StringUtils.getHhMmSs(),
                fileTypeCd = SbasConst.FileTypeCd.IMAGE,
                fileNm = fileInfo.filename,
                loclPath = fileInfo.localPath,
                uriPath = fileInfo.uriPath,
            )
            baseAttcRepository.persist(item)
            return CommonResponse(res)
        }
        return null
    }

    @Transactional
    fun findInfoPt(param: SearchParameters): CommonResponse<*> {
        val map = mutableMapOf<String, Any>()
        addIfNotNull(map, "gndr", param.gndr)
        addIfNotNull(map, "nati_cd", param.natiCd)
        addIfNotNull(map, "dstr_1_cd", param.dstr1Cd)
        addIfNotNull(map, "dstr_2_cd", param.dstr2Cd)
        val res = mutableMapOf<String, Any>()

        if (map.isEmpty()) {
            val infoPtList = infoPtRepository.findAll().list()
            res["items"] = infoPtList
            res["count"] = infoPtList.count()
            return CommonResponse(res)
        }

        val query = map.entries.stream()
            .map { entry -> "${entry.key}='${entry.value}'" }
            .collect(Collectors.joining(" and "))

//        log.debug("res========> $query")
        val infoPtList = infoPtRepository.find(query).list()
        res["items"] = infoPtList
        res["count"] = infoPtList.count()

        return CommonResponse(res)
    }

    @Transactional
    fun updateInfoPt(ptId: String, infoPtDto: InfoPtDto): CommonResponse<String> {
        val findInfoPt = infoPtRepository.findById(ptId) ?: throw NotFoundException("$ptId not found")
        findInfoPt.updateEntity(infoPtDto)
        return CommonResponse("$ptId 수정 성공")
    }

    @Transactional
    fun findInfoPtWithMyOrgan(): CommonResponse<*> {
        // infoUser 에 있는 dstrCd를 사용해도 되나?
        val infoUser = infoUserRepository.findById(jwt.name) ?: throw NotFoundException()
        val infoInst = infoInstRepository.findById(infoUser.instId!!) ?: throw NotFoundException()
        val infoPtList = infoPtRepository.findByDstrCd(infoInst.dstrCd1!!, infoInst.dstrCd2!!)

        val res = mutableMapOf<String, Any>()
        res["items"] = infoPtList
        res["count"] = infoPtList.count()

        return CommonResponse(res)
    }

    @Transactional
    fun findInfoPt2(param: SearchParameters): CommonResponse<*> {
        val entityManager = infoPtRepository.getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder

        val criteriaQuery: CriteriaQuery<InfoPt> = criteriaBuilder.createQuery(InfoPt::class.java)
        val root: Root<InfoPt> = criteriaQuery.from(InfoPt::class.java)

        val predicates: MutableList<Predicate> = mutableListOf()

        if (param.gndr != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("gndr"), param.gndr))
        }

        if (param.natiCd != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("natiCd"), param.natiCd))
        }

        if (param.dstr1Cd != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("dstr1Cd"), param.dstr1Cd))
        }

        if (param.dstr2Cd != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("dstr2Cd"), param.dstr2Cd))
        }

        if (predicates.isNotEmpty()) {
            criteriaQuery.where(*predicates.toTypedArray())
        }

        val query = entityManager.createQuery(criteriaQuery)

        val infoPtList = query.resultList
        val res = mutableMapOf<String, Any>()
        res["items"] = infoPtList
        res["count"] = infoPtList.count()

        return CommonResponse(res)
    }

    private fun addIfNotNull(map: MutableMap<String, Any>, key: String, value: String?) {
        if (!value.isNullOrBlank()) {
            map[key] = value
        }
    }

    private fun addIfNotNull(criteriaBuilder: CriteriaBuilder, criteriaQuery: CriteriaQuery<*>, value: String?) {
        val root: Root<InfoPt> = criteriaQuery.from(InfoPt::class.java)
        val predicates: MutableList<Predicate> = mutableListOf()
        if (!value.isNullOrBlank()) {
            predicates.add(criteriaBuilder.equal(root.get<String>(value), value))
        }
    }
}
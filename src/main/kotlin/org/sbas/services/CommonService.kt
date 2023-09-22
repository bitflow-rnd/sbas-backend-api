package org.sbas.services

import io.quarkus.cache.CacheKey
import org.jboss.logging.Logger
import org.sbas.dtos.*
import org.sbas.dtos.info.DelNoticeReq
import org.sbas.dtos.info.ModNoticeReq
import org.sbas.dtos.info.RegNoticeReq
import org.sbas.dtos.info.RegTermsReq
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.base.BaseCodeId
import org.sbas.repositories.BaseCodeEgenRepository
import org.sbas.repositories.BaseCodeRepository
import org.sbas.repositories.InfoTermsRepository
import org.sbas.repositories.NoticeRepository
import org.sbas.responses.CommonResponse
import org.sbas.utils.CustomizedException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class CommonService {

    @Inject
    private lateinit var log: Logger

    @Inject
    private lateinit var baseCodeRepository: BaseCodeRepository

    @Inject
    private lateinit var egenCodeRepository: BaseCodeEgenRepository

    @Inject
    private lateinit var noticeRepository: NoticeRepository

    @Inject
    private lateinit var termsRepository: InfoTermsRepository

    /**
     * 공통코드 그룹 목록 조회
     */
    @Transactional
    fun findBaseCdGrpList(): CommonResponse<*> {
        return CommonResponse(baseCodeRepository.findBaseCodeGrpList())
    }

    /**
     * 공통코드 그룹 등록
     */
    @Transactional
    fun saveBaseCodeGrp(saveReq: BaseCodeGrpSaveReq): CommonResponse<String> {
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(saveReq.cdGrpId)
        if (baseCodeGrp != null) {
            throw CustomizedException("${saveReq.cdGrpId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeRepository.persist(saveReq.toEntity())

        return CommonResponse("cdGrpId: ${saveReq.cdGrpId}")
    }

    /**
     * 공통코드 그룹 수정
     */
    @Transactional
    fun updateBaseCdGrp(updateReq: BaseCodeGrpUpdateReq): CommonResponse<String> {
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(updateReq.cdGrpId)
            ?: throw NotFoundException("${updateReq.cdGrpId} not found")

        // 코드 그룹 항목 수정
        baseCodeGrp.changeBaseCodeGrp(updateReq.cdGrpNm, updateReq.rmk)

        // 같은 코드 그룹 ID인 항목들 변경
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(updateReq.cdGrpId)
        if (baseCodeList.isNotEmpty()) {
            baseCodeList.forEach { it.changeBaseCodeGrpNm(updateReq.cdGrpNm) }
            return CommonResponse("수정 성공")
        } else {
            throw NotFoundException("${updateReq.cdGrpId} not found")
        }
    }

    /**
     * 공통코드 그룹 삭제
     */
    @Transactional
    fun deleteBaseCdGrp(cdGrpId: String): CommonResponse<String> {
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId)
        val baseCodeGrp = baseCodeRepository.findBaseCodeGrp(cdGrpId) ?: throw NotFoundException("$cdGrpId not found")

        // cdSeq 가 0이 아닌 항목들이 존재하면 삭제 X
        if (baseCodeList.any { it.cdSeq != 0 }) {
            throw CustomizedException("하위 항목들이 존재합니다.", Response.Status.CONFLICT)
        }

        baseCodeRepository.delete(baseCodeGrp)

        return CommonResponse("삭제 성공")
    }

    /**
     * 공통코드 목록 조회
     */
    @Transactional
//    @CacheResult(cacheName = "cdGrpId")
    fun findBaseCodeList(@CacheKey cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        val baseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
        return toBaseCodeResponse(baseCodeList)
    }

    /**
     * 공통코드 등록
     */
    @Transactional
    fun saveBaseCode(saveReq: BaseCodeSaveReq): CommonResponse<String> {
        baseCodeRepository.findBaseCodeGrp(saveReq.cdGrpId) ?: throw NotFoundException("공통코드 그룹이 없습니다.")
        val findBaseCode = baseCodeRepository.findById(BaseCodeId(saveReq.cdGrpId, saveReq.cdId))
        if (findBaseCode != null) {
            if (!findBaseCode.isCdGrpNmMatch(saveReq)) {
                throw CustomizedException("코드그룹 이름이 일치하지 않습니다.", Response.Status.BAD_REQUEST)
            }
            throw CustomizedException("${saveReq.cdId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeRepository.persist(saveReq.toEntity())

        return CommonResponse("cdId: ${saveReq.cdId}")
    }

    /**
     * 공통코드 수정
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun updateBaseCode(updateReq: BaseCodeUpdateReq): CommonResponse<String> {
        val baseCodeId = updateReq.getId()
        val findBaseCode = baseCodeRepository.findById(baseCodeId)
            ?: throw NotFoundException("${baseCodeId.cdId} not found")

        findBaseCode.changeBaseCode(updateReq)

        return CommonResponse("수정 성공")
    }

    /**
     * 공통코드 삭제
     */
    @Transactional
//    @CacheInvalidate(cacheName = "cdGrpId")
    fun deleteBaseCode(cdId: String): CommonResponse<String> {
        val findBaseCode = baseCodeRepository.findBaseCodeByCdId(cdId) ?: throw NotFoundException("$cdId not found")

        baseCodeRepository.delete(findBaseCode)

        return CommonResponse("삭제 성공")
    }

    /**
     * 시/도 목록 조회
     */
    @Transactional
//    @CacheResult(cacheName = "sido")
    fun findSidoList(): CommonResponse<List<BaseCodeResponse>> {
        val findBaseCodeList = baseCodeRepository.findBaseCodeByCdGrpId("SIDO")
        return toBaseCodeResponse(findBaseCodeList)
    }

    /**
     * 시/군/구 목록 조회
     * @param cdGrpId 시/도의 코드 그룹 ID ex) SIDO11, SIDO26...
     */
    @Transactional
//    @CacheResult(cacheName = "cdGrpId")
    fun findGugunList(@CacheKey cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        return when {
            cdGrpId.matches(Regex("^(SIDO)\\d+")) -> {
                val findBaseCodeList = baseCodeRepository.findBaseCodeByCdGrpId(cdGrpId = cdGrpId)
                toBaseCodeResponse(findBaseCodeList)
            }
            else -> throw NotFoundException("${cdGrpId}는 시/도의 코드 그룹 ID가 아닙니다.")
        }
    }

    /**
     * E-GEN 공통코드 목록 조회
     * @param cmMid 대분류 코드
     */
    @Transactional
//    @CacheResult(cacheName = "cmMid")
    fun findCodeEgenList(cmMid: String): CommonResponse<List<BaseCodeEgen>> {
        return CommonResponse(egenCodeRepository.findCodeEgenByCmMid(cmMid = cmMid))
    }

    /**
     * 공지사항 등록
     */
    @Transactional
    fun regNotice(regNoticeReq: RegNoticeReq): CommonResponse<String> {
        val infoNotice = regNoticeReq.toEntity()

        noticeRepository.persist(infoNotice)

        return CommonResponse("success")
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    fun modNotice(modNoticeReq: ModNoticeReq): CommonResponse<String> {
        val findNotice = noticeRepository.findById(modNoticeReq.noticeId)
            ?: throw NotFoundException("${modNoticeReq.noticeId} not found")

        findNotice.title = modNoticeReq.title
        findNotice.content = modNoticeReq.content
        findNotice.isActive = modNoticeReq.isActive
        findNotice.noticeType = modNoticeReq.noticeType
        findNotice.isUnlimited = modNoticeReq.isUnlimited
        findNotice.attcGrpId = modNoticeReq.attcGrpId

        return CommonResponse("success")
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    fun delNotice(delNoticeReq: DelNoticeReq): CommonResponse<String> {
        val findNotice = noticeRepository.findById(delNoticeReq.noticeId)
            ?: throw NotFoundException("${delNoticeReq.noticeId} not found")

        noticeRepository.delete(findNotice)

        return CommonResponse("success")
    }

    /**
     * 약관 등록
     */
    @Transactional
    fun regTerms(regTermsReq: RegTermsReq): CommonResponse<String>{
        val maxTermsVersion = termsRepository.findByTermsType(regTermsReq.termsType)

//        val regInfoTerms = regTermsReq.toEntity()
//
//        termsRepository.persist(regInfoTerms)

        return CommonResponse("success")
    }

    /**
     * BaseCode -> BaseCodeResponse 변환
     */
    private fun toBaseCodeResponse(baseCodeList: List<BaseCode>) =
        CommonResponse(baseCodeList.map {
            BaseCodeResponse(
                cdGrpId = it.id.cdGrpId,
                cdGrpNm = it.cdGrpNm,
                cdId = it.id.cdId,
                cdNm = it.cdNm,
                cdVal = it.cdVal,
                cdSeq = it.cdSeq,
                rmk = it.rmk,
            )
        }.toList())
}
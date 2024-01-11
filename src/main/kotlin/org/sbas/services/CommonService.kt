package org.sbas.services

import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import org.jboss.logging.Logger
import org.sbas.dtos.*
import org.sbas.dtos.info.*
import org.sbas.entities.base.BaseCode
import org.sbas.entities.base.BaseCodeEgen
import org.sbas.entities.info.*
import org.sbas.repositories.*
import org.sbas.responses.CommonResponse
import org.sbas.responses.notice.NoticeListResponse
import org.sbas.responses.terms.AgreeTermsListResponse
import org.sbas.responses.terms.TermsDetailResponse
import org.sbas.utils.CustomizedException
import org.sbas.utils.StringUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.sbas.component.base.BaseCodeReader
import org.sbas.entities.base.BaseCodeId


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

    @Inject
    private lateinit var termsAgreementRepository: TermsAgreementRepository

    @Inject
    private lateinit var noticeReadStatusRepository: NoticeReadStatusRepository

    @Inject
    private lateinit var baseCodeReader: BaseCodeReader

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
        val baseCodeGrp = baseCodeReader.getBaseCodeGrp(saveReq.cdGrpId)
        if (baseCodeGrp != null) {
            throw CustomizedException("${saveReq.cdGrpId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeReader.saveBaseCode(saveReq.cdGrpId, saveReq.toEntity())

        return CommonResponse("save cdGrpId: ${saveReq.cdGrpId}")
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
        val baseCodes = baseCodeReader.getBaseCodes(updateReq.cdGrpId)
        baseCodes.forEach { it.changeBaseCodeGrpNm(updateReq.cdGrpNm) }

        return CommonResponse("수정 성공")
    }

    /**
     * 공통코드 그룹 삭제
     */
    @Transactional
    fun deleteBaseCdGrp(cdGrpId: String): CommonResponse<String> {
        val baseCodeList = baseCodeReader.getBaseCodes(cdGrpId)
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
    fun findBaseCodeList(cdGrpId: String): CommonResponse<List<BaseCodeResponse>> {
        val baseCodes = baseCodeReader.getBaseCodes(cdGrpId)
        return toBaseCodeResponse(baseCodes)
    }

    /**
     * 공통코드 코드명 조회
     */
    @Transactional
    fun findBaseCodeName(cdId: String): CommonResponse<String> {
        val baseCode = baseCodeReader.getBaseCodeByCdId(cdId = cdId)
        return CommonResponse(baseCode.id.cdId)
    }

    /**
     * 공통코드 등록
     */
    @Transactional
    fun saveBaseCode(saveReq: BaseCodeSaveReq): CommonResponse<String> {
        val findBaseCode = baseCodeRepository.findById(BaseCodeId(saveReq.cdGrpId, saveReq.cdId))
        if (findBaseCode != null) {
            throw CustomizedException("${saveReq.cdId} 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        }
        baseCodeReader.saveBaseCode(saveReq.cdGrpId, saveReq.toEntity())

        return CommonResponse("cdId: ${saveReq.cdId}")
    }

    /**
     * 공통코드 수정
     */
    @Transactional
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
    fun deleteBaseCode(cdId: String): CommonResponse<String> {
        val findBaseCode = baseCodeReader.getBaseCodeByCdId(cdId)
        baseCodeRepository.delete(findBaseCode)
        return CommonResponse("삭제 성공")
    }

    /**
     * 시/도 목록 조회
     */
    @Transactional
    @CacheResult(cacheName = "sido")
    fun findSidoList(): CommonResponse<List<BaseCodeResponse>> {
        val baseCodes = baseCodeReader.getBaseCodes("SIDO")
        return toBaseCodeResponse(baseCodes)
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
                val baseCodes = baseCodeReader.getBaseCodes(cdGrpId)
                toBaseCodeResponse(baseCodes)
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
        findNotice.startNoticeDt = modNoticeReq.startNoticeDt
        findNotice.startNoticeTm = modNoticeReq.startNoticeTm
        findNotice.endNoticeDt = modNoticeReq.endNoticeDt
        findNotice.endNoticeTm = modNoticeReq.endNoticeTm
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
    fun regTerms(regTermsReq: RegTermsReq): CommonResponse<String> {
        val maxTermsVersion = termsRepository.findTermsVersionByTermsType(regTermsReq.termsType)
        val nextVersion = (maxTermsVersion.toIntOrNull() ?: 0) + 1
        val formattedVersion = nextVersion.toString().padStart(2, '0')

        val regInfoTerms = regTermsReq.toEntity(formattedVersion)

        termsRepository.persist(regInfoTerms)

        return CommonResponse("success")
    }

    /**
     * 약관 수정
     */
    @Transactional
    fun modTerms(modTermsReq: ModTermsReq): CommonResponse<String> {
        val termsVersion = modTermsReq.termsVersion
            ?: termsRepository.findTermsVersionByTermsType(modTermsReq.termsType)
        val termsId = InfoTermsId(termsType = modTermsReq.termsType, termsVersion = termsVersion, effectiveDt = modTermsReq.effectiveDt)

        val findTerms = termsRepository.findById(termsId)
            ?: throw NotFoundException("No terms and conditions of this type found")

        findTerms.detail = modTermsReq.detail

        return CommonResponse("${findTerms.termsName} 내용 수정 완료")
    }

    /**
     * 약관 삭제
     */
    @Transactional
    fun delTerms(delTermsReq: DelTermsReq): CommonResponse<String> {
        val termsId = InfoTermsId(termsType = delTermsReq.termsType, termsVersion = delTermsReq.termsVersion, effectiveDt = delTermsReq.effectiveDt)
        val findTerms = termsRepository.findById(termsId)
            ?: throw NotFoundException("No terms and conditions of this type found")

        termsRepository.delete(findTerms)

        return CommonResponse("${findTerms.termsName} 버전 ${termsId.termsVersion} 삭제 완료")
    }

    /**
     * 약관 목록
     */
    @Transactional
    fun getTermsByTermsType(termsType: String): CommonResponse<List<InfoTerms>> {
        val result: List<InfoTerms> = if (termsType == "00") {
            termsRepository.findAll().list()
        } else {
            termsRepository.findTermsListByTermsType(termsType)
        }

        return CommonResponse(result)
    }

    /**
     * 약관 상세
     */
    @Transactional
    fun getTermsDetailByTermsType(termsType: String, termsVersion: String): CommonResponse<TermsDetailResponse>{
        val findInfoTerms = if(termsVersion == "00") termsRepository.findRecentTermsByTermsType(termsType)
        else termsRepository.findTermsByTermsTypeAndTermsVersion(termsType, termsVersion)  ?: throw NotFoundException("not found this type terms")

        val result = TermsDetailResponse(
            termsType = termsType,
            detail = findInfoTerms.detail,
            termsVersion = findInfoTerms.id.termsVersion,
            termsName = findInfoTerms.termsName!!,
            effectiveDt = findInfoTerms.id.effectiveDt,
        )

        return CommonResponse(result)
    }

    /**
     * 약관 동의
     */
    @Transactional
    fun termsAgree(termsAgreeReq: TermsAgreeReq): CommonResponse<String> {
        val termsVersion = termsRepository.findTermsVersionByTermsType(termsAgreeReq.termsType)
        val termsId = TermsAgreementId(userId = termsAgreeReq.userId, termsType = termsAgreeReq.termsType, termsVersion = termsVersion)

        val saveAgreement =
            TermsAgreement(
                id = termsId,
                agreeYn = "Y",
                agreeDt = StringUtils.getYyyyMmDd(),
                agreeTm = StringUtils.getHhMm()
            )
        termsAgreementRepository.persist(saveAgreement)

        return CommonResponse("success")
    }

    /**
     * 동의한 약관 목록
     */
    @Transactional
    fun getAgreeTermsList(agreeTermsListReq: AgreeTermsListReq): CommonResponse<List<AgreeTermsListResponse>> {
        val findTermsList = termsAgreementRepository.findAgreeTermsListByUserId(agreeTermsListReq.userId, agreeTermsListReq.termsType)

        return CommonResponse(findTermsList)
    }

    /**
     * 공지사항 목록 조회(paging, total count 반영, filter O)
     */
    @Transactional
    fun getNoticeList(noticeListReq: NoticeListReq): CommonResponse<PagingListDto> {
        val findNotice = noticeRepository.findAllNoticeList(noticeListReq)
        val totalCnt = noticeRepository.count()
        val result = PagingListDto(totalCnt, findNotice as MutableList<NoticeListResponse>)
        return CommonResponse(result)
    }

    /**
     * 공지사항 상세 조회
     */
    @Transactional
    fun getNoticeDetail(noticeId: String): CommonResponse<InfoNotice> {
        val result = noticeRepository.findById(noticeId) ?: throw NotFoundException("not found this notice")

        return CommonResponse(result)
    }

    /**
     * 공지사항 읽기
     */
    @Transactional
    fun readNotice(noticeReadStatusId: NoticeReadStatusId): CommonResponse<String> {
        val inputData = NoticeReadStatus(noticeReadStatusId, StringUtils.getYyyyMmDd(), StringUtils.getHhMm())

        noticeReadStatusRepository.persist(inputData)

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
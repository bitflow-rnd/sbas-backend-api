package org.sbas.entities.info

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.sbas.entities.CommonEntity
import org.sbas.entities.StringPrefixedSequenceIdGenerator
import org.sbas.responses.notice.NoticeListResponse
import java.io.Serial
import java.io.Serializable
import javax.persistence.*

/**
 * 공지사항 정보
 */
@Entity
@Table(name = "info_notice")
class InfoNotice(

    @Column(name = "title", nullable = false, length = 30)
    var title: String, // 제목

    @Column(name = "content", nullable = false, length = 2000)
    var content: String, // 내용

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean? = null, // 활성화 여부(true = 활성, false = 비활성)

    @Column(name = "notice_type", nullable = false, length = 1)
    var noticeType: Char, // 공지 타입(B = 일반, N = 공지)

    @Column(name = "start_notice_dt", nullable = false, length = 8)
    var startNoticeDt: String, // 공지 시작 날짜

    @Column(name = "start_notice_tm", nullable = false, length = 2)
    var startNoticeTm: String, // 공지 시작 시간

    @Column(name = "end_notice_dt", nullable = false, length = 8)
    var endNoticeDt: String? = null, // 공지 종료 날짜

    @Column(name = "end_notice_tm", nullable = false, length = 2)
    var endNoticeTm: String? = null, // 공지 종료 시간

    @Column(name = "is_unlimited", nullable = false)
    var isUnlimited: Boolean? = null, // 기간 무제한 여부(true = 무제한, false = 제한)

    @Column(name = "attc_grp_id")
    var attcGrpId: String? = null // 첨푸 그룹 ID

) : CommonEntity(), Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_seq")
    @GenericGenerator(
        name = "notice_seq",
        strategy = "org.sbas.entities.StringPrefixedSequenceIdGenerator",
        parameters = [
            Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "NO"),
            Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d"),
            Parameter(name = StringPrefixedSequenceIdGenerator.incrementSize, value = "1")
        ])
    @Column(name = "notice_id", nullable = false, length = 10)
    lateinit var noticeId: String // 공지사항 id
        protected set

    companion object{
        @Serial
        private const val serialVersionUID: Long = -1968163549813218566L
    }

    fun toListResponse(isRead: Boolean): NoticeListResponse{
        return NoticeListResponse(
            noticeId = noticeId,
            title = title,
            content = content,
            noticeType = noticeType,
            startNoticeDt = startNoticeDt,
            isRead = isRead
        )
    }
}
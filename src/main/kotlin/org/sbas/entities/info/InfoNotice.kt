package org.sbas.entities.info

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.sbas.entities.CommonEntity
import org.sbas.entities.StringPrefixedSequenceIdGenerator
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
    var title: String? = null, // 제목

    @Column(name = "content", nullable = false, length = 2000)
    var content: String? = null, // 내용

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean? = null, // 활성화 여부(true = 활성, false = 비활성)

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
}
package org.sbas.constants

object ComnCode {
  const val ROOT_GRP_CODE = "0000"
}

const val MONGO_ID_LENGTH = 24

const val API_ROOT = "/api/v1/"
const val WEBHOOK_ROOT = "/wbhk/v1/"
const val WEBSOCKET_ROOT = "/wbsk/v1/"

object RspsCode {
  const val SUCCESS = 200
  const val REGISTERED = 201
  const val NO_AUTH = 401
  const val NOT_FOUND = 404
  const val SVR_ERROR = 500

  const val USER_EXIST = 900
  const val REG_WITH_OTHER = 901
  const val EMAIL_NOT_VERIFIED = 902
}

object UserRgstFailResn {
  const val SUCCESS = "SUCCESS"
  const val EXISTS = "EXISTS"
}

object IpHeaders {
  const val HEAD_01 = "X-Forwarded-For"
  const val HEAD_02 = "Proxy-Client-IP"
  const val HEAD_03 = "WL-Proxy-Client-IP"
  const val HEAD_04 = "HTTP_X_FORWARDED_FOR"
  const val HEAD_05 = "HTTP_X_FORWARDED"
  const val HEAD_06 = "HTTP_X_CLUSTER_CLIENT_IP"
  const val HEAD_07 = "HTTP_CLIENT_IP"
  const val HEAD_08 = "HTTP_FORWARDED_FOR"
  const val HEAD_09 = "HTTP_FORWARDED"
  const val HEAD_10 = "HTTP_VIA"
  const val HEAD_11 = "REMOTE_ADDR"
}

val IP_HEADER_CANDIDATES = arrayOf(
  IpHeaders.HEAD_01,
  IpHeaders.HEAD_02,
  IpHeaders.HEAD_04,
  IpHeaders.HEAD_04,
  IpHeaders.HEAD_05,
  IpHeaders.HEAD_06,
  IpHeaders.HEAD_07,
  IpHeaders.HEAD_08,
  IpHeaders.HEAD_09,
  IpHeaders.HEAD_10,
  IpHeaders.HEAD_11
)

object BotUser {
  const val G8ID_SCHD_USER = "!!G8ID-SCHD-USER!!"
  const val PDDL_WBHK_USER = "!!PADL-WBHK-USER!!"
  const val STRP_WBHK_USER = "!!STRP-WBHK-USER!!"
  const val PAPL_WBHK_USER = "!!PAPL-WBHK-USER!!"
}

object BackOfficePermission {
  const val MODRTR_G1 = "BUPM0000"
  const val MODRTR_G2 = "BUPM0001"
  const val SW_DEV = "BUPM0002"
  const val SYS_MGR = "BUPM0003"
  const val SUPER_ADM = "BUPM0009"
  val ALL = arrayOf(
    MODRTR_G1,
    MODRTR_G2,
    SW_DEV,
    SYS_MGR,
    SUPER_ADM
  )
}

object AgeVerified {
  const val NO = "USVG0000"
  const val TEEN = "USVG0001"
  const val ADULT = "USVG0002"
  const val ADULT_P = "USVG0003"
}

object CreatorRgstStats {
  const val NO = "CTST0000"
  const val REQ_VERIFY = "CTST0001"
  const val VERIFIED = "CTST0002"
}

object UserClass {
  const val GENERAL = "General"
  const val CREATOR = "Creator"
}

object FrontOfficePermission {
  const val GUEST = "FUPM0000"
  const val SIGN_IN = "FUPM0001"
  const val PERM_EVRYONE = "FUPM0002"
  const val PERM_TEEN = "FUPM0003"
  const val PERM_ADULT = "FUPM0004"
  const val PERM_ADULT_P = "FUPM0005"
}

object UserStatus {
  const val IN_REQUEST = "USTT0000"
  const val ACTIVE = "USTT0001"
  const val DORMANCY = "USTT0002"
  const val WITHDRAWAL = "USTT0003"
  const val BANNED = "USTT0004"
  const val LOCKED = "USTT0005"
  const val NOT_FOUND = "USTT0006"
  const val TOKN_EXPIRED = "USTT0007" // JWT token expired
  const val NO_AUTH = "USTT0008" // Not authorized
  const val WRONG_PW = "USTT0009"
}

object UpdateMode {
  const val APPROVE = "aprv"
  const val DENY = "deny"
  const val UPDATE = "updt"
  const val REMOVE = "remv"
}

object Days {
  const val ONE = 1L
  const val TWO = 2L
  const val THREE = 3L
  const val FOUR = 4L
  const val FIVE = 5L
  const val SIX = 6L
  const val WEEK = 7L
}

object Seconds {
  const val SEC = 1L
  const val DAY = 86_500L
  const val WEEK = 604_800L
}

object AuthProvider {
  const val EMAIL = "e"
  const val FACEBOOK = "f"
  const val GOOGLE = "g"
}

object Payoneer {
  object Account {
    object Status {
      // Card/Account not issued
      val NOT_ISSUED = 0

      // Card issued but not activated
      val ISSUED_NOT_ACTIVATED = 1

      // Card/Account Issued and activated
      val ACTIVATED = 2

      // Card/Account blocked
      val BLOCKED = 99

      // Card cancelled
      val CANCELLED = 100

      // Card lost or stolen
      val LOST_OR_STOLEN = 101
    }
  }
}

object CodeGrup {
  val MIDJR_VERS = "MJVR"
  val NJJR_VERS = "NJVR"
  val DALLE_VERS = "DEVR"
  val CREATOR_JOIN_STEP = "CTST"
  val CREATOR_GRADE = "CTGD"
  val ART_RGST_STEPS_MJ = "RGMJ"
  val GEN_TOOLS_CURR_AVAIL = "GNTL"
  val MEDIA_AVAIL_GRADES = "MAAG"
  val ART_SORT_STANDARD = "AORD"
  val USER_ACTIVE_STATUS = "USTT"
  val CREATOR_CLAIM_CODE = "CLRN"
  val GLOBAL_CONTINENTS = "CTCT"
  val UPSCALERS = "MJUP"
  val BOFC_USER_PERMISSION = "BUPM"
  val CONTENT_RIGHT_ISSUE = "UDCG"
  val IMAGE_ASPECT_RATIO = "APRT"
  val PURCHASE_STATUS = "PCST"
  val REPORT_REASON = "RPRS"
  val REJECT_REASON = "REJT"
  val REFUND_REASON = "RFND"

}

object Creator {
  const val TYPE_INDIVIDUAL = "INDIVIDUAL"
  const val TYPE_BUSINESS = "BUSINESS"

  const val GOVRN_ID_TYPE_PASSPORT = "PASSPORT"
  const val GOVRN_ID_TYPE_ID_CARD = "NATIONAL_ID_CARD"

  const val BIZ_TYPE_CORP = "CORPORATION"
  const val BIZ_TYPE_PRIV = "PRIVATE_COMPANY"
  const val BIZ_TYPE_PARTNER = "PARTNERSHIP"
  const val BIZ_TYPE_NON_PROFIT = "NOT_FOR_PROFIT_ORGANIZATION"
  const val BIZ_TYPE_GOV = "GOVERNMENT_ENTITY"
  const val BIZ_TYPE_PUBLIC = "PUBLIC_COMPANY"

}

object ScheduleTask {
  const val OLD_REQUEST_IMAGE = "SJTK0000" // Old request image deletion task (7d+)
  const val OLD_REQUEST_DATA = "SJTK0001" // Old request data deletion (7d+)
  const val OLD_CART_ITEM = "SJTK0002" // Old cart item data deletion (30d+)
  const val OLD_ACCESS_DATA = "SJTK0003" // Old access data deletion (Back/Cstm user sign-in)
  const val OLD_PAID_HISTORY = "SJTK0004" // Old paid history data deletion task (90d+)
  const val CONFIRM_PAID_HISTORY = "SJTK0005" // Confirm paid history
}

object OgMeta {
  const val OG_LOCALE = "en_US"
  const val OG_SITE_NAME = "Generated"
  const val OG_COPYRIGHTS = "https://gen8.id"
  const val LOGO_IMG_URL = "/aset/img/logo-generated.webp"
  const val OG_WEBPAGE_TYPE = "website"
  const val OG_TITLE = "Marketplace of generative AI prompts and generated media"
  const val TWITTER_CARD_PHOTO = "photo"    // one of summary, photo, player
  const val AUTHOR = "Generated, Inc."
  const val COPYRIGHT = "Generated, Inc."
  const val CREATOR_HUMAN = "Human"
  const val KEYWORDS =
    "reddit,generative,ai,prompt,midjourney,stablediffusion,nijijourney,image,media,art,picture,photo,chatgpt,openai,bard,llama,ecommerce,e-commerce,reward,creator,generated,generate,generation,llm"
  const val DESCRIPTION = "High quality prompt & media output submission guarantees high rewards. " +
    "Grow your creativeness with the generative AI marketplace."
}

object MetaTitle {
  const val TERM = "Terms of Service"
  const val PRVC = "Private Policy"
  const val CRTR = "Creator Consent"
  const val PRIC = "Pricing"
  const val FAQ = "FAQ"
  const val RANK = "Creator Rankings"
  const val IMGS = "Marketplace - Prompts and Images"
  const val RELS_NOTE = "Release notes"
}

object SiteMap {
  const val TOS = "polc_tos"
  const val PRVC = "polc_prvc"
  const val CRTR = "polc_crtr"
  const val PRIC = "polc_pric"
  const val FAQ = "polc_faq"
  const val RELS_NOTE = "polc_rlnt"
  const val RANK = "rank"
  const val IMGS = "pmpt_imgs"
}

enum class MetaTitleMap(val title: String) {
  TOS("Terms of Service"),
  PRVC("Private Policy"),
  CRTR("Creator Consent"),
  PRIC("Pricing"),
  FAQ("FAQ"),
  RANK("Creator Rankings"),
  IMGS("Marketplace"),
  RELS_NOTE("Release notes")
}

enum class MetaDescMap(val title: String) {
  TOS("Terms of Service"),
  PRVC("Private Policy"),
  CRTR("Creator Consent"),
  PRIC("Pricing"),
  FAQ("FAQ"),
  RANK("Creator Rankings"),
  IMGS("Marketplace"),
  RELS_NOTE("Release notes")
}

object Purchase {
  const val NOT_CONFIRMED = "PCST0000"
  const val CONFIRMED = "PCST0001"
  const val IN_DISPUTE = "PCST0002"
  const val CANCELED = "PCST0003"
}

object KywdGrup {
  const val SEARCH = "abcd000000000000"
  const val UPLOAD = "fedc000000000000"
}

// "CD" to "Cardinal number",
val KeywordPart = arrayOf(
  "FW"     // Foreign word
  , "JJ"   // Adjective
  , "JJR"  // Adjective, comparative
  , "JJS"  // Adjective, superlative
  , "LS"   // List item marker
  , "NN"   // Noun, singular or mass
  , "NNS"  // Noun, plural
  , "NNP"  // Proper noun, singular
  , "NNPS" // Proper noun, plural
  , "RB"   // Adverb
  , "RBR"  // Adverb, comparative
  , "RBS"  // Adverb, superlative
  , "SYM"  // Symbol
  , "VB"   // Verb, base form
  , "VBD"  // Verb, past tense
  , "VBG"  // Verb, gerund or present participle
  , "VBN"  // Verb, past participle
)

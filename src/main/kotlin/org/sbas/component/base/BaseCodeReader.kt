package org.sbas.component.base

import io.quarkus.cache.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.sbas.entities.base.BaseCode
import org.sbas.repositories.BaseCodeRepository

@ApplicationScoped
class BaseCodeReader(
    private val baseCodeRepository: BaseCodeRepository,
    @CacheName("baseCode") private val cache: Cache
) {

    fun getBaseCodeGrpList(): List<BaseCode> {
        return baseCodeRepository.findBaseCodeGrpList() ?: throw NotFoundException("")
    }

    @CacheResult(cacheName = "baseCodeGrp")
    fun getBaseCodeGrp(@CacheKey cdGrpId: String): BaseCode {
        val baseCodes = baseCodeRepository.findBaseCodeGrpList() ?: throw NotFoundException("")
        return baseCodes.firstOrNull { baseCode -> baseCode.id.cdGrpId == cdGrpId }
            ?: throw NotFoundException("$cdGrpId 해당하는 코드 그룹이 존재하지 않습니다.")
    }

    @CacheResult(cacheName = "baseCode")
    fun getBaseCodeList(@CacheKey cdGrpId: String): List<BaseCode> {
        val baseCodes = baseCodeRepository.findAllByCdGrpId(cdGrpId)
        return baseCodes
    }

  /**
   * 2024.07.03 승인대기목록에서 익셉션 발생 우회 처리, 그 외 수정필요사항은 필요 시 나중에 할것
   */
    fun getBaseCodeCdNm(cdGrpId: String, stringCode: String?): List<String>? {

      if (stringCode.isNullOrBlank()) {
        return null
      }
        val completableFuture =
            cache.`as`(CaffeineCache::class.java).getIfPresent<List<BaseCode>>(cdGrpId)
        val baseCodes = if (completableFuture == null) {
            getBaseCodeList(cdGrpId)
        } else {
            completableFuture.get()
        }

      println("cdGrpId/stringCode $cdGrpId $stringCode")

        val list = stringCode.split(";")?.map { code ->
            baseCodes.firstOrNull { it.id.cdId == code }?.cdNm ?: throw NotFoundException("Code not found - $code")
        }
        return list
    }

    fun getBaseCodeByCdId(cdId: String): BaseCode {
        return baseCodeRepository.findByCdId(cdId) ?: throw NotFoundException("해당 코드($cdId)를 찾을 수 없습니다.")
    }
}
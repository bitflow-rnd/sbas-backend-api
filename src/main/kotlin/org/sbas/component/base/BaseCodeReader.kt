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

    fun getBaseCodeCdNm(cdGrpId: String, stringCode: String?): List<String>? {
        val completableFuture =
            cache.`as`(CaffeineCache::class.java).getIfPresent<List<BaseCode>>(cdGrpId)
        val baseCodes = if (completableFuture == null) {
            getBaseCodeList(cdGrpId)
        } else {
            completableFuture.get()
        }

        val list = stringCode?.split(";")?.map { code ->
            baseCodes.firstOrNull { it.id.cdId == code }?.cdNm ?: throw NotFoundException("해당 코드를 찾을 수 없습니다.")
        }
        return list
    }

    fun getBaseCodeByCdId(cdId: String): BaseCode {
        return baseCodeRepository.findByCdId(cdId) ?: throw NotFoundException("해당 코드($cdId)를 찾을 수 없습니다.")
    }
}
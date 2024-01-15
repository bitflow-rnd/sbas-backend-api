package org.sbas.component.base

import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheKey
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import org.sbas.entities.base.BaseCode
import org.sbas.repositories.BaseCodeRepository
import org.sbas.utils.CustomizedException

@ApplicationScoped
class BaseCodeProcessor(
    private val baseCodeRepository: BaseCodeRepository
) {

    @CacheInvalidate(cacheName = "baseCodeGrp")
    fun saveBaseCodeGrp(@CacheKey cdGrpId: String, baseCode: BaseCode): BaseCode {
        val baseCodes = baseCodeRepository.findAllByCdGrpId(cdGrpId)
        if (!baseCodes.isNullOrEmpty()) {
            throw CustomizedException("$cdGrpId 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        } else {
            baseCodeRepository.persist(baseCode)
        }
        return baseCode
    }

    @CacheInvalidate(cacheName = "baseCode")
    fun saveBaseCode(@CacheKey cdGrpId: String, baseCode: BaseCode): BaseCode {
        val findBaseCode = baseCodeRepository.findByCdId(baseCode.id.cdId)
        if (findBaseCode != null) {
            throw CustomizedException("$cdGrpId 이미 등록되어 있습니다.", Response.Status.CONFLICT)
        } else {
            baseCodeRepository.persist(baseCode)
        }
        return baseCode
    }

    @CacheInvalidate(cacheName = "baseCode")
    fun removeBaseCode(@CacheKey cdGrpId: String, baseCode: BaseCode): BaseCode {
        baseCodeRepository.delete(baseCode)
        return baseCode
    }
}
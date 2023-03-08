package org.sbas.utils

import org.eclipse.microprofile.jwt.JsonWebToken
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.SecurityContext

/**
 * JWT 등 토큰관련 처리 도움 유틸
 */
class TokenUtils {
    companion object {
        fun hasJwt(jwt: JsonWebToken): Boolean {
            return jwt.getClaimNames() != null
        }

        fun debugJwtContent(jwt: JsonWebToken, ctx: SecurityContext): String {
            val name = if (ctx.getUserPrincipal() == null) {
                "anonymous"
            } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
                throw InternalServerErrorException("Principal and JsonWebToken names do not match")
            } else {
                ctx.getUserPrincipal().getName()
            }
            return "hello $name isHttps: ${ctx.isSecure()} authScheme: ${ctx.getAuthenticationScheme()}" +
                    " hasJWT: ${hasJwt(jwt)}"
        }
    }
}
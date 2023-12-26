package org.sbas.utils

import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.JsonWebToken
import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext


/**
 * JWT 등 토큰관련 처리 도움 유틸
 */
class TokenUtils {

    companion object {

        @Produces(MediaType.TEXT_PLAIN)
        fun helloRolesAllowed(@Context ctx: SecurityContext, jwt: JsonWebToken): String {
            return debugJwtContent(jwt, ctx)// + ", birthdate: " + jwt.claim<Any?>("birthdate").toString()
        }
        private fun hasJwt(jwt: JsonWebToken): Boolean {
            return jwt.claimNames != null
        }

        fun debugJwtContent(jwt: JsonWebToken, ctx: SecurityContext): String {
            val name = if (ctx.userPrincipal == null) {
                "anonymous"
            } else if (!ctx.userPrincipal.name.equals(jwt.name)) {
                throw InternalServerErrorException("Principal and JsonWebToken names do not match")
            } else {
                ctx.userPrincipal.name
            }

            return "hello $name isHttps: ${ctx.isSecure} authScheme: ${ctx.authenticationScheme} hasJWT: ${hasJwt(jwt)}" +
                    " issueAt: ${jwt.issuedAtTime}" + " expiresAt: ${jwt.expirationTime} userType: ${jwt.groups}"
        }

        fun generateUserToken(userId: String, userNm: String): String {
            return Jwt.issuer("http://dev.smartbas.org")
                    .upn(userId)
                    .subject(userNm)
                    .claim("userNm", userNm)
                    .expiresIn(60 * 60 * 24 * 30)//30일
                    .groups("USER")
                    .sign()
        }

        fun generateAdminToken(userId: String, userNm: String): String {
            return Jwt.issuer("http://dev.smartbas.org")
                    .upn(userId)
                    .subject(userNm)
//                    .claim("userNm", userNm)
                    .expiresIn(60 * 60 * 24 * 30)
                    .groups("ADMIN")
                    .sign()
        }

    }
}
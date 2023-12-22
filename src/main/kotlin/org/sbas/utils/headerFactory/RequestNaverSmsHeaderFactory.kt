package org.sbas.utils.headerFactory

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap

@ApplicationScoped
class RequestNaverSmsHeaderFactory : ClientHeadersFactory{

    @ConfigProperty(name = "restclient.naversens.serviceid")
    private lateinit var naversensserviceid: String

    @ConfigProperty(name = "restclient.naversens.access-key")
    private lateinit var accessKey: String

    @ConfigProperty(name = "restclient.naversens.secret-key")
    private lateinit var secretKey: String

    override fun update(incomingHeaders: MultivaluedMap<String, String>?,
                        clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {

        val result: MultivaluedMap<String, String> = MultivaluedHashMap()

        val timestamp = System.currentTimeMillis().toString()

        val signature = makeSignature("POST", timestamp)

        result.add("Content-Type", "application/json")
        result.add("x-ncp-apigw-timestamp", timestamp)
        result.add("x-ncp-iam-access-key", accessKey)
        result.add("x-ncp-apigw-signature-v2", signature)

        return result
    }

    fun makeSignature(method: String, timestamp: String): String{

        val message = StringBuilder()
                .append(method)
                .append(" ")
                .append("/sms/v2/services/$naversensserviceid/messages")
                .append("\n")
                .append(timestamp)
                .append("\n")
                .append(accessKey)
                .toString()

        val signingKey = SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "HmacSHA256")
        val mac: Mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

        val rawHmac: ByteArray = mac.doFinal(message.toByteArray(charset("UTF-8")))

        return Base64.encodeBase64String(rawHmac)
    }

}
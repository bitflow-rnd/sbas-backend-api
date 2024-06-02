package org.sbas.utils.headerFactory

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory

@ApplicationScoped
class RequestNaverGeocodingHeaderFactory : ClientHeadersFactory{

    @ConfigProperty(name = "restclient.navergeocoding.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "restclient.navergeocoding.client-secret")
    lateinit var clientSecret: String

    override fun update(incomingHeaders: MultivaluedMap<String, String>?,
                        clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {

        val result: MultivaluedMap<String, String> = MultivaluedHashMap()

        result.add("Accept", "application/json")
        result.add("X-NCP-APIGW-API-KEY-ID", clientId)
        result.add("X-NCP-APIGW-API-KEY", clientSecret)

        return result
    }

}
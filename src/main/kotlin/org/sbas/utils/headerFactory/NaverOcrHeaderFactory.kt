package org.sbas.utils.headerFactory

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap

@ApplicationScoped
class NaverOcrHeaderFactory : ClientHeadersFactory{

    @ConfigProperty(name = "restclient.naverocr.secret.key")
    lateinit var secretkey: String

    override fun update(incomingHeaders: MultivaluedMap<String, String>?,
                        clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {

        val result: MultivaluedMap<String, String> = MultivaluedHashMap()
        result.add("X-OCR-SECRET", secretkey)
        return result
    }

}
package org.sbas.handlers

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import org.sbas.constants.NaverApiConst
import org.sbas.restclients.NaverOcrRestClient
import org.sbas.restparameters.NaverOcrApiParams
import org.sbas.restparameters.OcrApiImagesParam
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


/**
 * 사용자를 CRUD하는 서비스 클래스
 */
@ApplicationScoped
class NaverApiHandler {

    @Inject
    lateinit var log: Logger

    @RestClient
    lateinit var naverOcrClient: NaverOcrRestClient

    @ConfigProperty(name = "restclient.naverocr.secret.key")
    lateinit var secretkey: String

    @ConfigProperty(name = "domain.this")
    lateinit var serverDomain: String

    @ConfigProperty(name = "upload.path.relative")
    lateinit var uploadRelPath: String


    fun recognizeImage(param: String): String {
        val dotIdx = param.lastIndexOf(".")
        val image = OcrApiImagesParam(
            param.substring(dotIdx + 1),
            "edpireportimg",
            null,
            "$serverDomain/$uploadRelPath/$param"
        )
        val now = System.currentTimeMillis()
        val images = mutableListOf<OcrApiImagesParam>()
        images.add(image)
        val reqparam = NaverOcrApiParams(
            images,
            NaverApiConst.ClovaOcr.VERSION,
            now.toString(),
            now,
            NaverApiConst.ClovaOcr.LANG
        )
        val res = naverOcrClient.recognize(reqparam)
        val texts = res.images[0].fields
        val textlist = ArrayList<String>()
        for (field in texts!!) {
            textlist.add(field.inferText!!)
        }
        return textlist.joinToString("|")
    }

}
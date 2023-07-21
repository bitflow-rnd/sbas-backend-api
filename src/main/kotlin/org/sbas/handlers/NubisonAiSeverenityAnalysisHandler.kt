package org.sbas.handlers

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.sbas.restclients.NubisonAiSeverenityAnalysisRestClient
import org.sbas.services.SvrtService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class NubisonAiSeverenityAnalysisHandler {

    @RestClient
    private lateinit var nubisonAiSeverenityAnalysisRestClient: NubisonAiSeverenityAnalysisRestClient

    @Inject
    private lateinit var svrtService: SvrtService

    fun analyse(pid: String): String {

        val requestData = svrtService.getSvrtRequestData(pid)
        val requestBody = """
            {
                "content_type": "pd",
                "inputs": [
                    {
                        "data": [
                            $requestData
                        ],
                        "datatype": "BYTES",
                        "name": "data",
                        "parameters": {
                            "content_type": "str"
                        },
                        "shape": [
                            ${requestData.length}
                        ]
                    }
                ]
            }
        """.replace("\n", "")

        return nubisonAiSeverenityAnalysisRestClient.infer(requestBody).toString()
    }

}
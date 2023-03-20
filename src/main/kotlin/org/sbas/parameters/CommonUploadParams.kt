package org.sbas.parameters

import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.jboss.resteasy.reactive.PartType
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.io.File
import javax.validation.constraints.NotNull
import javax.ws.rs.core.MediaType

@Schema(description = "공통 - 업로드 파라미터")
data class CommonUploadParams (

    @RestForm
//    @PartType(MediaType.APPLICATION_JSON)
//    @PartType(MediaType.TEXT_PLAIN)
    var userId: String, // CommonUploadData,

//    @NotNull
//    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @RestForm(FileUpload.ALL)
    var file: File

)

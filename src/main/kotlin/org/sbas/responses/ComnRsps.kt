package org.sbas.responses

import org.sbas.constants.RspsCode
import org.sbas.utils.annotation.NoArg
import java.io.Serializable

// code is for JSON inner status, not http status code
// eror is detailed err msg
@NoArg
public open class ComnRsps<T> (var rslt: T?) {

    constructor (): this(null)

    var code: Int? = RspsCode.SUCCESS
    var mesg: String? = null

    fun setMsgNotFound(msg: String): ComnRsps<T> {
        if(rslt == null) {
            this.mesg = "Not Found Any $msg"
        }
        return this
    }
}

data class PaginatedRslt<out A, out B>(
    val items: A,
    val count: B
) : Serializable
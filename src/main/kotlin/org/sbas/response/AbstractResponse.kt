package org.sbas.response

abstract class AbstractResponse<T> {
    var code: String? = "00"
    var message: String? = null
    abstract var result: T?
}
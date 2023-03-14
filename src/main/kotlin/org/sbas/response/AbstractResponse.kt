package org.sbas.response

import org.eclipse.microprofile.openapi.annotations.media.Schema

abstract class AbstractResponse<T> {
    lateinit var code: String
    lateinit var message: String
    abstract var result: T?
}
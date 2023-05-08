package org.sbas.utils

import javax.websocket.Decoder
import javax.websocket.Encoder
import javax.websocket.Extension
import javax.websocket.HandshakeResponse
import javax.websocket.server.HandshakeRequest
import javax.websocket.server.ServerEndpointConfig

class CustomWebsocketConfigurator : ServerEndpointConfig.Configurator() {

    override fun modifyHandshake(conf: ServerEndpointConfig?, request: HandshakeRequest?, response: HandshakeResponse?) {
        val maxFrameSize = 1048576

        val customParameter = CustomExtension.CustomParameter("max-frame-size", maxFrameSize.toString())
        val customExtension = CustomExtension("permessage-deflate", listOf(customParameter))

        val extensions = conf?.extensions?.toMutableList()
        extensions?.add(customExtension)

        val customConfig = conf?.let { CustomServerEndpointConfig(it, extensions) }


        super.modifyHandshake(customConfig, request, response)
    }
}

class CustomExtension(private val name: String, private val parameters: List<Extension.Parameter>) : Extension {
    override fun getName(): String {
        return name
    }

    override fun getParameters(): List<Extension.Parameter> {
        return parameters
    }

    class CustomParameter(private val name: String, private val value: String) : Extension.Parameter {
        override fun getName(): String {
            return name
        }

        override fun getValue(): String {
            return value
        }
    }
}

class CustomServerEndpointConfig(private val original: ServerEndpointConfig, private val newExtensions: List<Extension>?) : ServerEndpointConfig {
    override fun getEndpointClass(): Class<*> {
        return original.endpointClass
    }

    override fun getPath(): String {
        return original.path
    }

    override fun getConfigurator(): ServerEndpointConfig.Configurator {
        return original.configurator
    }

    override fun getUserProperties(): MutableMap<String, Any> {
        return original.userProperties
    }

    override fun getSubprotocols(): List<String> {
        return original.subprotocols
    }

    override fun getExtensions(): MutableList<Extension> {
        return newExtensions?.toMutableList() ?: original.extensions
    }

    override fun getEncoders(): MutableList<Class<out Encoder>>? {
        return original.encoders
    }

    override fun getDecoders(): MutableList<Class<out Decoder>>? {
        return original.decoders
    }
}
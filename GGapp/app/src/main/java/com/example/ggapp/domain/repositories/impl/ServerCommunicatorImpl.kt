package com.example.ggapp.domain.repositories.impl

import android.util.Log
import com.example.ggapp.domain.repositories.interfaces.ServerCommunicator
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class ServerCommunicatorImpl(private val customHost: String, private val customPort: Int):
    ServerCommunicator {

    override val host: String = customHost
    override val port: Int = customPort

    private var socket: Socket? = null
    private var output: OutputStream? = null
    private var input: BufferedReader? = null

    override fun sendMessage(message: String): String {
        socket = Socket(host, port)
        output = socket?.getOutputStream()
        input = BufferedReader(InputStreamReader(socket?.getInputStream()))

        output?.write(message.toByteArray())
        var line: String
        while (input!!.readLine().also { line = it } != null) {
            Log.d("ServerCommunicator", line)
        }

        input?.close()
        output?.close()
        socket?.close()
        return line
    }
}
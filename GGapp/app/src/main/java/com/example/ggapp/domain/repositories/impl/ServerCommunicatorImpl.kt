package com.example.ggapp.domain.repositories.impl

import com.example.ggapp.domain.repositories.interfaces.ServerCommunicator
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket

class ServerCommunicatorImpl(
    private val customHost: String,
    private val customPort: Int
):
    ServerCommunicator {

    override val host: String = customHost
    override val port: Int = customPort

    private lateinit var socket: Socket
    private lateinit var output: OutputStream
    private lateinit var input: BufferedReader
    override fun sendMessage(messageToSend: String): String {
        socket = Socket(host, port)

        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = OutputStreamWriter(socket.getOutputStream())

//        TODO: change write to writeBytes
        writer.write(messageToSend)
        writer.flush()

        val serverResponse = readTillEndOfMessage(reader)

        reader.close()
        writer.close()
        socket.close()

        return serverResponse
    }

    private fun readTillEndOfMessage(reader: BufferedReader): String {
        val buffer = CharArray(1024)
        val serverResponse = StringBuilder()
        var bytesRead: Int
        var foundEndOfMessage = false

        while (reader.read(buffer).also { bytesRead = it } != -1 && !foundEndOfMessage) {
            serverResponse.appendRange(buffer, 0, bytesRead)

            if (serverResponse.contains("\n\n")) {
                foundEndOfMessage = true

                serverResponse.setLength(serverResponse.indexOf("\n\n"))

                break
            }
        }
        return serverResponse.toString()
    }
}
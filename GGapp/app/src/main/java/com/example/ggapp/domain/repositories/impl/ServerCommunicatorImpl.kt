package com.example.ggapp.domain.repositories.impl

import android.util.Log
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
        Log.d("ServerCommunicatorImpl", "Socket created")

        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = OutputStreamWriter(socket.getOutputStream())
        Log.d("ServerCommunicatorImpl", "Streams created")

        writer.write(messageToSend)
        Log.d("ServerCommunicatorImpl", "Message sent")
        writer.flush()

        val serverResponse = reader.readLine()
        Log.d("ServerCommunicatorImpl", "Response received $serverResponse")

        reader.close()
        writer.close()
        socket.close()

        return serverResponse
    }
}
package com.example.ggapp.domain.repositories.interfaces

interface ServerCommunicator {
    val host: String
    val port: Int

    fun sendMessage(message: String): String
}
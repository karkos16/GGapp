package com.example.ggapp.domain.repositories.interfaces

interface ServerCommunicator {
    /**
     * Host of the server to connect to.
     */
    val host: String
    /**
     * Port of the server to connect to.
     */
    val port: Int

    /**
     * Sends a message to the server and returns the response.
     * @param messageToSend - message to send to the server.
     * @return [String] response from the server.
     */
    fun sendMessage(messageToSend: String): String
}
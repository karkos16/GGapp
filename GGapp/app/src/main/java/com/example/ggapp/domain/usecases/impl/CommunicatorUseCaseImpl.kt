package com.example.ggapp.domain.usecases.impl

import com.example.ggapp.domain.repositories.impl.ServerCommunicatorImpl
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import kotlinx.coroutines.delay

class CommunicatorUseCaseImpl(private val serverCommunicator: ServerCommunicatorImpl) :
    CommunicatorUseCase {


    override suspend fun getID(): String {
        delay(1000)
        return serverCommunicator.sendMessage("0000\n\n")
    }

    override suspend fun addFriend(friend: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getMessages(): String {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: String, receiver: String): String {
        TODO("Not yet implemented")
    }
}
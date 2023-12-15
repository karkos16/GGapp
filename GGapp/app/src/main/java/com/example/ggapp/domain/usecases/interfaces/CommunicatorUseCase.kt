package com.example.ggapp.domain.usecases.interfaces

interface CommunicatorUseCase {
    suspend fun getID(): String
    suspend fun addFriend(id:String, friendID: String): Boolean;
    suspend fun getMessages(): String
    suspend fun sendMessage(message: String, receiver: String): String
}
package com.example.ggapp.domain.usecases

interface CommunicatorUseCase {
    suspend fun getID(): String
    suspend fun addFriend(friend: String): String;
    suspend fun getMessages(): String
    suspend fun sendMessage(message: String, receiver: String): String
}
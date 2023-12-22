package com.example.ggapp.domain.usecases.interfaces

import com.example.ggapp.presentation.viewModels.Message
import com.example.ggapp.presentation.viewModels.UserInfo

interface CommunicatorUseCase {
    suspend fun getID(): String
    suspend fun addFriend(id:String, friendID: String): Boolean;
    suspend fun getMessages(id: String, friendID: String): List<Message>
    suspend fun sendMessage(message: String, receiver: String, id: String): Boolean
    suspend fun getContacts(id: String): List<UserInfo>
    fun getIsSocketClosed(): Boolean
}
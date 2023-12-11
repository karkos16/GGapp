package com.example.ggapp.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Message(
    val sender: String,
    val content: String
)

class ConversationViewModel: ViewModel(){

    val messages = mutableStateListOf<Message>()

    var messageToSent by mutableStateOf("")
        private set

    fun onMessageChange(newValue: String){
        messageToSent = newValue
    }

    fun sendMessage() {
        messageToSent = ""
    }

    fun getMessagesFromServer() {
        messages.addAll(listOf(
            Message("1", "Hello"),
            Message("2", "Hi"),
            Message("1", "How are you?????????????????????????????????????????????????????????????????????????????????????????"),
            Message("2", "Good, you?"),
            Message("1", "Good too!"),
            Message("1", "Bye!")
        ))
    }
}
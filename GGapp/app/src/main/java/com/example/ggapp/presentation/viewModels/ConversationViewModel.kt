package com.example.ggapp.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class Message(
    val sender: String,
    val content: String
)

class ConversationViewModel(
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
): ViewModel(){

    val messages = mutableStateListOf<Message>()

    var messageToSent by mutableStateOf("")
        private set

    var fetchingMessagesStatus by mutableStateOf(false)
        private set

    fun onMessageChange(newValue: String){
        messageToSent = newValue
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendMessage(contactID: String) {
        val tempMessage = messageToSent
        GlobalScope.launch(Dispatchers.IO) {
            communicatorUseCase.sendMessage(
                message = tempMessage,
                receiver = contactID,
                id = sharedPrefsRepository.getIDFromPreferences()
            )
            getMessagesFromServer(contactID)
        }
        messageToSent = ""
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getMessagesFromServer(contactID: String) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val id = sharedPrefsRepository.getIDFromPreferences()
            val gotMessages = communicatorUseCase.getMessages(id, contactID)
            messages.clear()
            messages.addAll(gotMessages)
            fetchingMessagesStatus = true
        }
    }
}
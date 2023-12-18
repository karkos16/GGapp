package com.example.ggapp.domain.usecases.impl

import android.util.Log
import com.example.ggapp.domain.repositories.impl.ServerCommunicatorImpl
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import com.example.ggapp.presentation.viewModels.Message
import com.example.ggapp.presentation.viewModels.UserInfo
import kotlinx.coroutines.delay

class CommunicatorUseCaseImpl(private val serverCommunicator: ServerCommunicatorImpl) :
    CommunicatorUseCase {


    override suspend fun getID(): String {
        return serverCommunicator.sendMessage("0000\n\n")
    }

    private fun validateID(id: String): Boolean {
        return id.length == 4 && id.toIntOrNull() != null
    }
    override suspend fun addFriend(id: String, friendID: String): Boolean {
        if (!validateID(friendID)) {
            return false
        }
        val response = serverCommunicator.sendMessage("0001$id$friendID\n\n")
        return response == "1"
    }

    override suspend fun getMessages(id: String, friendID: String): List<Message> {
        val response = serverCommunicator.sendMessage("0003$id$friendID\n\n") + "\n"
        return processMessageResponse(response)
    }

    private fun processMessageResponse(response: String): List<Message> {
        Log.d("CommunicatorUseCaseImpl", "Otrzymano wiadomosci: " + response)
        if (response == "NONE\n") {
            return listOf()
        }
        val resultList: MutableList<Message> = mutableListOf()
        val messageList = response.removeSuffix("kjasdflksajklafjkll\n").split("kjasdflksajklafjkll\n")
        Log.d("CommunicatorUseCaseImpl", "Liczba pobranych wiadomosci: " + messageList.size.toString())
        Log.d("CommunicatorUseCaseImpl", "Pobrane wiadomosci: $messageList")
        for (message in messageList) {
            val messageParts = message.split("klfjaklfsjalkfsjafklsaj\n")
            val sender = messageParts[0]
            val content = messageParts[1]
            resultList.add(Message(sender, content))
        }
        return resultList
    }

    override suspend fun sendMessage(message: String, receiver: String, id: String): Boolean {
        val response = serverCommunicator.sendMessage("0002$id$receiver$message\n\n")
        return response == "1"
    }

    override suspend fun getContacts(id: String): List<UserInfo> {
        val response = serverCommunicator.sendMessage("0004$id\n\n") + "\n"

        return processContactsResponse(response)
    }

    private fun processContactsResponse(response: String): List<UserInfo> {
        Log.d("CommunicatorUseCaseImpl", "Otrzymano kontakty: " + response)
        if (response == "NONE\n\n") {
            return listOf()
        }
        val resultList: MutableList<UserInfo> = mutableListOf()
        val contactList = response.removeSuffix("oiaiudusj\n").split("oiaiudusj\n")
        Log.d("CommunicatorUseCaseImpl", "Liczba pobranych kontaktow: " + contactList.size.toString())
        Log.d("CommunicatorUseCaseImpl", "Pobrane kontakty: $contactList")
        for (contact in contactList) {
            resultList.add(UserInfo(contact))
        }
        return resultList
    }
}
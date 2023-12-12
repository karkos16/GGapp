package com.example.ggapp.presentation.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ggapp.domain.usecases.CommunicatorUseCase

data class UserInfo(
    val id: String
)

class HomeViewModel(
    private val communicatorUseCase: CommunicatorUseCase,
    private val application: Application
): ViewModel() {



     var contacts = mutableStateListOf<UserInfo>()
        private set

    var showDialog by mutableStateOf(false)
        private set

    var newContactID by mutableStateOf("")
        private set

    fun addUser() {
//        TODO: connect to server
        contacts.add(UserInfo(newContactID))
    }

    fun showDialog(){
        showDialog = true
    }

    fun hideDialog(){
        showDialog = false
    }

    fun updateNewContactID(newValue: String) {
        newContactID = newValue
    }
}
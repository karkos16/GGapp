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

data class UserInfo(
    val id: String
)

class HomeViewModel(
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
): ViewModel() {

    var id by mutableStateOf("")
        private set

     var contacts = mutableStateListOf<UserInfo>()
        private set

    var showDialog by mutableStateOf(false)
        private set

    var newContactID by mutableStateOf("")
        private set

    var addingContactFailed by mutableStateOf(false)
        private set

    @OptIn(DelicateCoroutinesApi::class)
    fun addUser() {
        if (contacts.contains(UserInfo(newContactID))) {
            addingContactFailed = true
            newContactID = ""
            Log.d("HomeViewModel", "Kontakt już istnieje")
            return
        }

        val tempContactID = newContactID
        newContactID = ""
        GlobalScope.launch(Dispatchers.IO) {
            if (communicatorUseCase.addFriend(id, tempContactID)) {
                Log.d("HomeViewModel", "Dodano kontakt")
                contacts.add(UserInfo(tempContactID))
            } else {
                addingContactFailed = true
                Log.d("HomeViewModel", "Nie dodano kontaktu")
            }
        }
    }

    fun showDialog(){
        showDialog = true
    }

    fun hideDialog(){
        showDialog = false
    }

    fun updateNewContactID(newValue: String) {
        Log.d("HomeViewModel", "Nowe id: $newValue")
        newContactID = newValue
    }

    fun getIDFromPreferences(): String {
        id = sharedPrefsRepository.getIDFromPreferences()
        Log.d("HomeViewModel", "Pobrano id z shared preferences: $id")
        return id
    }

    fun updateAddingContactStatus() {
        addingContactFailed = false
    }
}
package com.example.ggapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
): ViewModel() {


    @OptIn(DelicateCoroutinesApi::class)
    fun getID(){
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Pobieranie id")
            val id = communicatorUseCase.getID()
            saveIDToSharedPreferences(id)
            Log.d("MainViewModel", "Dodano id do shared preferences: $id")
        }
    }

    private fun saveIDToSharedPreferences(id: String) {
        sharedPrefsRepository.saveIDToSharedPreferences(id)
    }

    fun getIDFromPreferences(): String {
        return sharedPrefsRepository.getIDFromPreferences()
    }
}
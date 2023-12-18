package com.example.ggapp.presentation.viewModels

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
            val id = communicatorUseCase.getID()
            saveIDToSharedPreferences(id[0].toString() + id[1].toString() + id[2].toString() + id[3].toString())
        }
    }

    private fun saveIDToSharedPreferences(id: String) {
        sharedPrefsRepository.saveIDToSharedPreferences(id)
    }

    fun getIDFromPreferences(): String {
        return sharedPrefsRepository.getIDFromPreferences()
    }
}
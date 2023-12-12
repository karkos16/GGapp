package com.example.ggapp.presentation.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ggapp.domain.usecases.CommunicatorUseCase
import com.example.ggapp.presentation.ui.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val communicatorUseCase: CommunicatorUseCase, private val application: Application): ViewModel() {

    private val sharedPreferences by lazy {
        application.applicationContext.getSharedPreferences("com.example.ggapp", Context.MODE_PRIVATE)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getID(){
        GlobalScope.launch(Dispatchers.IO) {
            val id = communicatorUseCase.getID()
            saveIDToSharedPreferences(id)
        }
    }

    private fun saveIDToSharedPreferences(id: String) {
        sharedPreferences.edit().putString("user_id", id).apply()
    }

    fun getIDFromPreferences(): String {
        return sharedPreferences.getString("user_id", null) ?: ""
    }
}
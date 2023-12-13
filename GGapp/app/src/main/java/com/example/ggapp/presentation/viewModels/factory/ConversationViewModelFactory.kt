package com.example.ggapp.presentation.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import com.example.ggapp.presentation.viewModels.ConversationViewModel

class ConversationViewModelFactory (
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(communicatorUseCase, sharedPrefsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
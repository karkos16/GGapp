package com.example.ggapp.presentation.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import com.example.ggapp.presentation.viewModels.MainViewModel

/**
 * Factory for creating a [MainViewModel] with a constructor that takes a
 * [CommunicatorUseCase] and a [SharedPrefsRepository].
 */
class MainViewModelFactory(
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(communicatorUseCase, sharedPrefsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.ggapp.presentation.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository
import com.example.ggapp.domain.usecases.interfaces.CommunicatorUseCase
import com.example.ggapp.presentation.viewModels.HomeViewModel

/**
 * Factory for creating a [HomeViewModel] with a constructor that takes a
 * [CommunicatorUseCase] and a [SharedPrefsRepository].
 */
class HomeViewModelFactory(
    private val communicatorUseCase: CommunicatorUseCase,
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(communicatorUseCase, sharedPrefsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
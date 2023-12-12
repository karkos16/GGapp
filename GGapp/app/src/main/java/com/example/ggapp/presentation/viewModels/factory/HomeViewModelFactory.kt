package com.example.ggapp.presentation.viewModels.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ggapp.domain.usecases.CommunicatorUseCase
import com.example.ggapp.presentation.viewModels.HomeViewModel
import com.example.ggapp.presentation.viewModels.MainViewModel

class HomeViewModelFactory(
    private val communicatorUseCase: CommunicatorUseCase,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return HomeViewModel(communicatorUseCase, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
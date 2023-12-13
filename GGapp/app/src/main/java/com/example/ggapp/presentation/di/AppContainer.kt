package com.example.ggapp.presentation.di

import android.content.Context
import com.example.ggapp.domain.repositories.impl.ServerCommunicatorImpl
import com.example.ggapp.domain.repositories.impl.SharedPrefsRepositoryImpl
import com.example.ggapp.domain.usecases.impl.CommunicatorUseCaseImpl
import com.example.ggapp.presentation.viewModels.ConversationViewModel
import com.example.ggapp.presentation.viewModels.HomeViewModel
import com.example.ggapp.presentation.viewModels.MainViewModel

class AppContainer(private val context: Context) {

    private val sharedPreferencesRepository: SharedPrefsRepositoryImpl by lazy {
        SharedPrefsRepositoryImpl(context.getSharedPreferences("com.example.ggapp", Context.MODE_PRIVATE))
    }

    private val serverCommunicator: ServerCommunicatorImpl by lazy {
        ServerCommunicatorImpl("10.0.2.2", 1234)
    }

    private val communicatorUseCase: CommunicatorUseCaseImpl by lazy {
        CommunicatorUseCaseImpl(serverCommunicator)
    }

    val mainViewModel: MainViewModel by lazy {
        MainViewModel(
            communicatorUseCase,
            sharedPreferencesRepository
        )
    }

    val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(
            communicatorUseCase,
            sharedPreferencesRepository
        )
    }

    val conversationViewModel: ConversationViewModel by lazy {
        ConversationViewModel(
            communicatorUseCase,
            sharedPreferencesRepository
        )
    }
}
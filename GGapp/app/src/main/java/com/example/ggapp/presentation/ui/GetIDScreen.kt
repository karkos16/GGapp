package com.example.ggapp.presentation.ui

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggapp.domain.repositories.impl.ServerCommunicatorImpl
import com.example.ggapp.domain.usecases.CommunicatorUseCaseImpl
import com.example.ggapp.presentation.ui.destinations.GetIDScreenDestination
import com.example.ggapp.presentation.ui.destinations.HomeScreenDestination
import com.example.ggapp.presentation.viewModels.MainViewModel
import com.example.ggapp.presentation.viewModels.factory.MainViewModelFactory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun GetIDScreen(
    navigator: DestinationsNavigator
) {
    val serverCommunicator = remember { ServerCommunicatorImpl("150.254.30.30", 25) }
    val communicatorUseCase = remember { CommunicatorUseCaseImpl(serverCommunicator) }
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(communicatorUseCase, application = Application()))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.getID()
            navigator.popBackStack(GetIDScreenDestination, true)
            navigator.navigate(HomeScreenDestination)
        }) {
            Text(text = "Get ID")
        }
    }
}
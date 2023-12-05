package com.example.ggapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggapp.presentation.ui.destinations.GetIDScreenDestination
import com.example.ggapp.presentation.ui.destinations.HomeScreenDestination
import com.example.ggapp.presentation.viewModels.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.startDestination

@RootNavGraph(start = true)
@Destination
@Composable
fun GetIDScreen(
    navigator: DestinationsNavigator
) {

    val viewModel = viewModel<MainViewModel>()

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
            Text(text = "Uzyskaj identyfikator")
        }
    }
}
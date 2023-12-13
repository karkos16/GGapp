package com.example.ggapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ggapp.presentation.di.AppContainer
import com.example.ggapp.presentation.ui.destinations.GetIDScreenDestination
import com.example.ggapp.presentation.ui.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun GetIDScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val appContainer = remember { AppContainer(context) }
    val viewModel = appContainer.mainViewModel

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
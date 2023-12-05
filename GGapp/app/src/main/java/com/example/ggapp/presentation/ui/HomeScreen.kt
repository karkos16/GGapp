package com.example.ggapp.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggapp.presentation.viewModels.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {

    val viewModel = viewModel<HomeViewModel>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "GGapp") },
                actions = {
                    AddContactButton()
                }
            )
        },
        content = {
            Content(viewModel, navigator)
        }
    )
}

@Composable
fun AddContactButton() {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add contact"
        )
    }
}

@Composable
fun Content(viewModel: HomeViewModel, navigator: DestinationsNavigator) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        viewModel.addedUsers.forEach { userInfo ->
            item {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = userInfo.id.toString() + " " + userInfo.username)
                }
            }

        }
    }
}
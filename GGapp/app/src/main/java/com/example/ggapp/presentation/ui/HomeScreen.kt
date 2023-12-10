package com.example.ggapp.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggapp.presentation.ui.destinations.ConversationScreenDestination
import com.example.ggapp.presentation.viewModels.HomeViewModel
import com.example.ggapp.presentation.viewModels.UserInfo
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
                    AddContactButton(viewModel)
                }
            )
        },
        content = {
            Content(viewModel, navigator)
        }
    )

    AddContactDialog(viewModel)
}

@Composable
fun AddContactButton(viewModel: HomeViewModel) {
    IconButton(onClick = { viewModel.showDialog() }) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add contact"
        )
    }
}

@Composable
fun AddContactDialog(viewModel: HomeViewModel) = if(viewModel.showDialog) {
    AlertDialog(
        onDismissRequest = { viewModel.hideDialog() },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.hideDialog()
                    viewModel.addUser()
            }) {
                Text(text = "Add Contact")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.hideDialog()}) {
                Text(text = "Cancel")
            }
        },
        text = {
            OutlinedTextField(
                value = viewModel.newContactID,
                onValueChange = { viewModel.updateNewContactID(it) },
                label = { Text("Identifier") }
            )
        }
    )
} else {

}

@Composable
fun Content(viewModel: HomeViewModel, navigator: DestinationsNavigator) {
    val contacts = remember {
        viewModel.contacts
    }
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 50.dp)) {
        contacts.forEach {userInfo ->
            item {
                TextButton(onClick = { navigator.navigate(ConversationScreenDestination(contactID = userInfo.id)) }) {
                    Text(text = userInfo.id)
                }
            }

        }
    }
}
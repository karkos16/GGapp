package com.example.ggapp.presentation.ui

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ggapp.presentation.di.AppContainer
import com.example.ggapp.presentation.ui.destinations.ConversationScreenDestination
import com.example.ggapp.presentation.viewModels.HomeViewModel
import com.example.ggapp.presentation.viewModels.factory.HomeViewModelFactory
import com.example.ggapp.presentation.viewModels.factory.MainViewModelFactory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val appContainer = remember { AppContainer(context) }
    val viewModel = appContainer.homeViewModel

//    Get ID from shared preferences asynchronously
    LaunchedEffect(Unit) {
        while (viewModel.id.isEmpty()) {
            viewModel.getIDFromPreferences()
            delay(500)
        }
        while (!viewModel.fetchingContactsEnded) {
            viewModel.getContacts()
            delay(500)
            viewModel.updateFetchingContactsStatus()
        }
    }

//    Checks if adding contact was successful
    LaunchedEffect(viewModel.addingContactFailed) {
        if (viewModel.addingContactFailed) {
            Toast.makeText(context, "Kontakt juz istnije lub podane id jest błędne", Toast.LENGTH_SHORT).show()
            viewModel.updateAddingContactStatus()
        }
    }

    Scaffold(
        topBar = {
            if (viewModel.id.isNotEmpty() or viewModel.fetchingContactsEnded) {
                TopAppBar(
                    title = {
                        Row {
                            Text(text = "GGapp")
                            Spacer(modifier = Modifier.weight(0.2f))
                            Text(text = "Twoje id: ${viewModel.id}")
                        } },
                    actions = {
                        AddContactButton(viewModel)
                    }
                )
            }
        },
        content = {
            Content(viewModel, navigator)
            if (viewModel.id.isEmpty() or !viewModel.fetchingContactsEnded) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()

                }
            }
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
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 50.dp)) {
        contacts.forEach {userInfo ->
            item {
                TextButton(onClick = { navigator.navigate(ConversationScreenDestination(contactID = userInfo.id)) }) {
                    Text(text = userInfo.id)
                }
            }

        }
    }
}
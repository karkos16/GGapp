package com.example.ggapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ggapp.presentation.di.AppContainer
import com.example.ggapp.presentation.viewModels.ConversationViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ConversationScreen(
    navigator: DestinationsNavigator,
    contactID: String
) {
    val context = LocalContext.current
    val appContainer = remember { AppContainer(context) }
    val viewModel = appContainer.conversationViewModel

    val messages = remember {
        viewModel.messages
    }

    LaunchedEffect(Unit) {
        viewModel.getMessagesFromServer(contactID = contactID)
        while (true) {
            delay(10000)
            viewModel.getMessagesFromServer(contactID = contactID)
        }
    }

    if (!viewModel.fetchingMessagesStatus) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                CircularProgressIndicator()
                Text(text = "Loading...")
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            TopAppBar(
                title = { Text(text = "Conversation with: $contactID") },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Home Screen"
                        )
                    }
                }
            )

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn {
                messages.forEach() { message ->
                    item {
                        Message(message.sender, message.content, contactID)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
            SendMessageBar(viewModel = viewModel, contactID = contactID)
        }
    }
}

@Composable
fun SendMessageBar(viewModel: ConversationViewModel, contactID: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val message = viewModel.messageToSent

        OutlinedTextField(
            value = message,
            onValueChange = { viewModel.onMessageChange(it) },
            label = { Text(text = "Message") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { viewModel.sendMessage(contactID) }, modifier = Modifier.padding(8.dp)) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Message"
            )
        }
    }
}

@Composable
fun Message(senderID: String, message: String, contactID: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (senderID != contactID) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.7f),
            contentAlignment = if (senderID != contactID) Alignment.BottomEnd else Alignment.BottomStart
        ) {
            Text(text = message,
                modifier = Modifier
                    .background(
                        color = if (senderID != contactID) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )
        }

    }
}
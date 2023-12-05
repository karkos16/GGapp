package com.example.ggapp.presentation.viewModels

import androidx.lifecycle.ViewModel

data class UserInfo(
    val id: Int,
    val username: String
)

class HomeViewModel: ViewModel() {



     var addedUsers = mutableListOf(
        UserInfo(
            1234,
            "Bob"
        ),
        UserInfo(
            4321,
            "John"
        ),
        UserInfo(
            9876,
            "Natalie"
        )
    )
        private set

    fun addUser(user: UserInfo) {
        addedUsers.add(user)
    }




}
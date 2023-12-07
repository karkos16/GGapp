package com.example.ggapp.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.ggapp.presentation.ui.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

class MainViewModel: ViewModel() {


    fun getID(): String{
//        TODO: connect to server and get ID
        return "1234"
    }
}
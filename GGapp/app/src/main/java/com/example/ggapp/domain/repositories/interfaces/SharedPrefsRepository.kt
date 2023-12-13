package com.example.ggapp.domain.repositories.interfaces

interface SharedPrefsRepository {
    fun saveIDToSharedPreferences(id: String)
    fun getIDFromPreferences(): String
}
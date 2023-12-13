package com.example.ggapp.domain.repositories.impl

import android.content.SharedPreferences
import android.util.Log
import com.example.ggapp.domain.repositories.interfaces.SharedPrefsRepository

class SharedPrefsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SharedPrefsRepository {
    override fun saveIDToSharedPreferences(id: String) {
        sharedPreferences.edit().putString("user_id", id).apply()
    }

    override fun getIDFromPreferences(): String {
        Log.d("SharedPrefsRepositoryImpl", "Pobieranie id z shared preferences")
        return sharedPreferences.getString("user_id", null) ?: ""
    }
}
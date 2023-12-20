package com.example.ggapp.domain.repositories.interfaces

interface SharedPrefsRepository {
    /**
     * Saves the user's ID to SharedPreferences
     * @param id the user's ID
     */
    fun saveIDToSharedPreferences(id: String)

    /**
     * Gets the user's ID from SharedPreferences
     * @return [String] the user's ID
     */
    fun getIDFromPreferences(): String
}
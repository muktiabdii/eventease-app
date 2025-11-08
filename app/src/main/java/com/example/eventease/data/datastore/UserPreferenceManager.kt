package com.example.eventease.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_preferences")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_UID = stringPreferencesKey("uid")
        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PHOTO = stringPreferencesKey("photo")
    }

    // save user info
    suspend fun saveUser(uid: String, name: String, email: String, photoUrl: String = "") {
        context.dataStore.edit { preferences ->
            preferences[KEY_UID] = uid
            preferences[KEY_NAME] = name
            preferences[KEY_EMAIL] = email
            preferences[KEY_PHOTO] = photoUrl
        }
    }

    // save hanya foto
    suspend fun updatePhoto(photoUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PHOTO] = photoUrl
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val userUid: Flow<String?> = context.dataStore.data.map { it[KEY_UID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_EMAIL] }
    val userPhoto: Flow<String?> = context.dataStore.data.map { it[KEY_PHOTO] }
}

package com.example.eventease.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// datastore preferences
private val Context.dataStore by preferencesDataStore("app_preferences")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_UID = stringPreferencesKey("uid")
        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_EMAIL = stringPreferencesKey("email")
    }

    // save user info
    suspend fun saveUser(uid: String, name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_UID] = uid
            preferences[KEY_NAME] = name
            preferences[KEY_EMAIL] = email
        }
    }

    // clear user info
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_UID)
            preferences.remove(KEY_NAME)
            preferences.remove(KEY_EMAIL)
        }
    }

    // get user uid flow
    val userUid: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_UID]
    }

    // get user name flow
    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_NAME]
    }

    // get user email flow
    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL]
    }
}
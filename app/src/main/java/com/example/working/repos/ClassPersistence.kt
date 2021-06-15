package com.example.working.repos

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.working.loginorsignup.TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val PREFERENCES_USER = "User_INFO"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_USER)

@Singleton
class ClassPersistence @Inject constructor(@ApplicationContext val context: Context) {

    val read = context.dataStore.data.catch { e ->
        if (e is IOException) {
            Log.i(TAG, "READ_EXCEPTION: ${e.localizedMessage}")
            emit(emptyPreferences())
        } else {
            throw e
        }
    }.map { preferences ->
        val email = preferences[data.EMAIL_ADDRESS] ?: ""
        val password = preferences[data.USER_PASSWORD] ?: ""
        val flag = preferences[data.REMEMBER_ME] ?: false
        UserStore(email, password, flag)
    }

    suspend fun updateInfo(email: String,password: String,flag: Boolean){
        context.dataStore.edit {mutablePreferences ->
            mutablePreferences[data.EMAIL_ADDRESS]=email
            mutablePreferences[data.USER_PASSWORD]=password
            mutablePreferences[data.REMEMBER_ME]=flag
        }
    }

    private val data = object {
        val EMAIL_ADDRESS = stringPreferencesKey("EMAIL_ADDRESS")
        val REMEMBER_ME = booleanPreferencesKey("Remember_Me")
        val USER_PASSWORD = stringPreferencesKey("PASSWORD")
    }
}

data class UserStore(
    val email: String,
    val password: String,
    val flag: Boolean
)

package idv.fanboat.kottoy.presentation.extenstions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

suspend fun <T> DataStore<Preferences>.putValue(key: String, value: T) {
    this.edit { it[stringPreferencesKey(key)] = Gson().toJson(value) }
}

inline fun <reified T> DataStore<Preferences>.getValue(key: String): Flow<T> {
    return this.data.map {
        KLog.e(key, it[stringPreferencesKey(key)])
        Gson().fromJson(it[stringPreferencesKey(key)], T::class.java)
    }
}
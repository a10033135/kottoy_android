package idv.fanboat.kottoy.presentation.extenstions

import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment

fun Fragment.showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

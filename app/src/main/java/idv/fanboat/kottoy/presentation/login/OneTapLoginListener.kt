package idv.fanboat.kottoy.presentation.login

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import java.lang.Exception

interface OneTapLoginListener {
    val loginResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    fun onLoginSuccess()
    fun onLoginOrVerifyFailure(e: Exception)
}
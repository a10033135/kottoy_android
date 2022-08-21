package idv.fanboat.kottoy.presentation.login

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.socks.library.KLog

class OneTapLoginGoogleHelper(private val loginListener: OneTapLoginListener) {

    private val TAG = OneTapLoginListener::class.java.simpleName
    private var oneTapClient: SignInClient? = null

    fun startLogin(activity: Activity, googleClientId: String) {
        oneTapClient = Identity.getSignInClient(activity))
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(googleClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient?.beginSignIn(signInRequest)
            ?.addOnSuccessListener(activity) { result ->
                try {
                    loginListener.onLogin()
                } catch (e: IntentSender.SendIntentException) {
                    KLog.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            ?.addOnFailureListener(activity) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
    }

}
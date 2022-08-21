package idv.fanboat.kottoy.presentation.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.socks.library.KLog
import idv.fanboat.kottoy.R
import java.lang.Exception

class OneTapLoginGoogleHelper(
    private val loginListener: OneTapLoginListener,
) {

    private val TAG = OneTapLoginListener::class.java.simpleName
    private var oneTapClient: SignInClient? = null

    fun startGoogleVerify(activity: Activity) {
        KLog.i(TAG, "startGoogleVerify")
        oneTapClient = Identity.getSignInClient(activity)
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.google_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient?.beginSignIn(signInRequest)
            ?.addOnSuccessListener(activity) { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                loginListener.loginResultLauncher.launch(intentSenderRequest)
            }
            ?.addOnFailureListener(activity) { e -> loginListener.onLoginOrVerifyFailure(e) }
    }

    fun startGoogleLogin(data: Intent) {
        KLog.i(TAG, "startGoogleLogin")
        val auth = Firebase.auth
        val googleCredential = oneTapClient?.getSignInCredentialFromIntent(data)
        val idToken = googleCredential?.googleIdToken
        when {
            idToken != null -> {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { loginListener.onLoginSuccess() }
                    .addOnFailureListener { loginListener.onLoginOrVerifyFailure(it) }
            }
            else -> {
                loginListener.onLoginOrVerifyFailure(Exception())
            }
        }
    }

}
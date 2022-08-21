package idv.fanboat.kottoy.presentation.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.socks.library.KLog
import idv.fanboat.kottoy.R
import idv.fanboat.kottoy.databinding.FragmentLoginBinding
import idv.fanboat.kottoy.presentation.base.BaseFragment


class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        isRoot: Boolean
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    private val TAG = LoginFragment::class.java.simpleName


    private val loginHelper by lazy {
        OneTapLoginGoogleHelper(object : OneTapLoginListener {
            override fun onLogin() {
                resultLauncher.launch()
            }

            override fun onSuccess() {
                TODO("Not yet implemented")
            }
        })
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val auth = Firebase.auth
            val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = googleCredential.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success")
                                val user = auth.currentUser

                                KLog.e(TAG, user?.email)
                                KLog.e(TAG, user?.phoneNumber)
//                            updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
//                            updateUI(null)
                            }
                        }
                }
                else -> {
                    // Shouldn't happen.
                    Log.d(TAG, "No ID token!")
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            loginHelper.startLogin(
                requireActivity(), requireActivity().getString(R.string.google_client_id)
            )
        }

    }
}
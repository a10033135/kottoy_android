package idv.fanboat.kottoy.presentation.login

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.socks.library.KLog
import idv.fanboat.kottoy.databinding.FragmentLoginBinding
import idv.fanboat.kottoy.model.User
import idv.fanboat.kottoy.presentation.base.BaseFragment
import idv.fanboat.kottoy.presentation.extenstions.dataStore
import idv.fanboat.kottoy.presentation.extenstions.putValue
import idv.fanboat.kottoy.presentation.extenstions.showToast
import idv.fanboat.kottoy.presentation.main.MainActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoginFragment : BaseFragment<FragmentLoginBinding>(), OneTapLoginListener {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        isRoot: Boolean
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    private val TAG = LoginFragment::class.java.simpleName

    private val loginHelper by lazy { OneTapLoginGoogleHelper(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
//            loginHelper.startGoogleVerify(requireActivity())

            Firebase.auth.signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 3)

        }
    }

    override fun onLoginSuccess() {
        KLog.i(TAG, "onLoginSuccess")


        val firebaseUser = User(email = Firebase.auth.currentUser?.email ?: "")

        lifecycleScope.launch {
            val user = FirebaseFirestore.getInstance()
                .collection("user")
                .whereEqualTo("email", firebaseUser.email)
                .get()
                .await()
                .first()
                .toObject(User::class.java)

            showToast("onLoginSuccess: $user").show()

            requireActivity().dataStore.putValue("User", user)
            requireActivity().setResult(RESULT_OK)
//            requireActivity().finish()

            ActivityNavigator(requireActivity()).apply {
                val destination =
                    this.createDestination().setIntent(Intent(requireActivity(), MainActivity::class.java))
//                destination.id = R.id.SecondFragment
                destination.setData("kottoy://Second".toUri())
                navigate(
//                    destination, null, NavOptions.Builder().setPopUpTo(R.id.SecondFragment, true, true).build(), null
                    destination, null, null, null
                )
            }

        }


    }

    override fun onLoginOrVerifyFailure(e: Exception) {
        KLog.e(TAG, e.message)
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
    }

    override val loginResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loginHelper.startGoogleLogin(result.data ?: android.content.Intent())
            }
        }


}
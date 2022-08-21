package idv.fanboat.kottoy.presentation.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.socks.library.KLog
import idv.fanboat.kottoy.databinding.FragmentLoginBinding
import idv.fanboat.kottoy.presentation.base.BaseFragment
import java.lang.Exception


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
            loginHelper.startGoogleVerify(requireActivity())
        }
    }

    override fun onLoginSuccess() {
        KLog.i(TAG, "onLoginSuccess")
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
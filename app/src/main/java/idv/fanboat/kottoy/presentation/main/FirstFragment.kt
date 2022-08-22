package idv.fanboat.kottoy.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.socks.library.KLog
import idv.fanboat.kottoy.databinding.FragmentFirstBinding
import idv.fanboat.kottoy.model.User
import idv.fanboat.kottoy.presentation.base.BaseFragment
import idv.fanboat.kottoy.presentation.extenstions.dataStore
import idv.fanboat.kottoy.presentation.extenstions.getValue
import idv.fanboat.kottoy.presentation.extenstions.showToast
import idv.fanboat.kottoy.presentation.login.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FirstFragment : BaseFragment<FragmentFirstBinding>() {

    private val onLogin = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        KLog.e("TAG", "onActivityResult: ${result.resultCode} ${result.data}")
        lifecycleScope.launch {
            val user = requireActivity().dataStore.getValue<User>("User").first()
            showToast("onLogin: ${user.email}").show()
            binding.textviewFirst.text = user.email
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        isRoot: Boolean
    ): FragmentFirstBinding {
        return FragmentFirstBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            onLogin.launch(intent)
        }
    }
}
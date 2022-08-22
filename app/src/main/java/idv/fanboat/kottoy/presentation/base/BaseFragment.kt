package idv.fanboat.kottoy.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?, isRoot: Boolean): VB

    private var _binding: VB? = null

    protected val binding: VB
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = createViewBinding(inflater, container, false)
        return binding.root
    }

    fun initView() {}
    fun initAction() {}
    fun observeData() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
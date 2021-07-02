package com.example.working.adminui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.AlluserFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.alluser.MyRecycleView
import com.example.working.utils.MySealed
import com.example.working.utils.userchannel.FireBaseUser
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


const val LOAD_SIZE = 3

@AndroidEntryPoint
class AllUserFragment : Fragment(R.layout.alluser_fragment) {
    private lateinit var binding: AlluserFragmentBinding
    private lateinit var myAdapterView: MyRecycleView
    private val myViewModel: MyViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AlluserFragmentBinding.bind(view)
        Log.i(TAG, "onViewCreated: ${myViewModel.loading}")
        if (myViewModel.loading==null || myViewModel.loading==true)
            binding.myShimmer.isVisible = true
        setRecycleView()
        getAllUsers()
        setUpUI()
        binding.root.setOnRefreshListener {
            myAdapterView.refresh()
        }
    }

    private fun dilog(message: String) {
        val action = AllUserFragmentDirections.actionGlobalPasswordDialog2("Error", message)
        findNavController().navigate(action)
    }

    private fun setUpUI() {
        lifecycleScope.launch {
            myAdapterView.loadStateFlow.collectLatest {
                when (it.append) {
                    is LoadState.NotLoading -> {
                        binding.root.isRefreshing = false
                        myViewModel.loading=false
                        Log.i(TAG, "setUpUI: Not Loading")
                    }
                    LoadState.Loading -> {
                        binding.root.isRefreshing = true
                        binding.myShimmer.isVisible = false
                        myViewModel.loading=false
                        Log.i(TAG, "setUpUI: User Is Loading")
                    }
                    is LoadState.Error -> {
                        binding.root.isRefreshing = false
                        myViewModel.loading=false
                        val error = (it.append as LoadState.Error).error.localizedMessage
                        if (error.equals("List is Empty", true))
                            dilog(message = "$error")
                        Log.i(TAG, "setUpUI: $error")
                    }
                }
            }
        }
    }

    private fun getAllUsers() {
        lifecycleScope.launch {
            myViewModel.flow.collectLatest {
                myAdapterView.submitData(it)
            }
        }
    }

    private fun setRecycleView() {
        binding.apply {
            myRecycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                myAdapterView = MyRecycleView { udi ->
                    itemClicked(udi)
                }
                adapter = myAdapterView
            }
        }
    }
    private fun itemClicked(udi: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Source Udi", udi)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(requireView(), "$udi is COPIED", Snackbar.LENGTH_SHORT).show()
    }
}
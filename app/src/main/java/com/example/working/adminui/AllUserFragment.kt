package com.example.working.adminui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.AlluserFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.alluser.MyRecycleView
import com.example.working.utils.MySealed
import com.example.working.utils.userchannel.FireBaseUser
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val LOAD = "LOADING"

@AndroidEntryPoint
class AllUserFragment : Fragment(R.layout.alluser_fragment) {
    private lateinit var binding: AlluserFragmentBinding
    private var loading: Boolean? = null
    private var loadOnlyOnce: Boolean = false

    @Inject
    lateinit var myAdapterView: MyRecycleView
    private val myViewModel: MyViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AlluserFragmentBinding.bind(view)
        setRecycleView()
        savedInstanceState?.let {
            loading = it.getBoolean(LOAD)
        }
        loading?.let {
            if (it) {
                myViewModel.getAllFireBaseUsers.clear()
                setData()
            }
        }
        if (myViewModel.getAllFireBaseUsers.isNotEmpty()  && !loadOnlyOnce) {
            Log.i(TAG, "onViewCreated: NOT Empty")
            myAdapterView.submitList(myViewModel.getAllFireBaseUsers)
        } else if (myViewModel.getAllFireBaseUsers.isEmpty()) {
            setData()
        }
        binding.root.setOnRefreshListener {
            loading = true
            myViewModel.getAllFireBaseUsers.clear()
            setData()
        }
    }

    private fun dialog(title: String = "Error!!", message: String) {
        val action = AllUserFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    private fun hideSwipeOrNot() {
        if (loading != null && loading == true) {
            binding.swipeToRefresh.isRefreshing = false
            loading = false
        } else
            binding.myShimmer.isVisible = false
    }

    private fun showSwipeOrNot() {
        if (loading != null && loading == true)
            binding.swipeToRefresh.isRefreshing = true
        else
            binding.myShimmer.isVisible = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setData() {
        myViewModel.getAllUser().observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    hideSwipeOrNot()
                    dialog(message = "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    showSwipeOrNot()
                }
                is MySealed.Success -> {
                    hideSwipeOrNot()
                    binding.myRecycleView.setBackgroundColor(
                        resources.getColor(
                            R.color.light_grey,
                            null
                        )
                    )
                    val documentSnapshot = it.data as MutableList<*>
                    documentSnapshot.forEach { list ->
                        val doc = list as DocumentSnapshot
                        if (doc.exists()) {
                            val fireBaseUser = doc.toObject(FireBaseUser::class.java)
                            fireBaseUser?.let { User ->
                                Log.i(TAG, "setData: $User")
                                User.id = doc.id
                                myViewModel.getAllFireBaseUsers.add(User)
                            }
                        }
                    }
                    if (myViewModel.getAllFireBaseUsers.isNotEmpty()) {
                        Log.i(TAG, "onViewCreated: ${myViewModel.getAllFireBaseUsers}")
                        loadOnlyOnce = true
                        myAdapterView.submitList(myViewModel.getAllFireBaseUsers)
                    }
                }
            }
        }
    }

    private fun setRecycleView() {
        binding.apply {
            myRecycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myAdapterView
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        loading?.let {
            outState.putBoolean(LOAD, it)
        }
    }
}
package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.NoteFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.resource.ResourcesRecycleView
import com.example.working.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val RESOURCES_LOAD_SIZE = 1

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.note_framgnet) {
    private lateinit var binding: NoteFramgnetBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private lateinit var resourcesRecycleView: ResourcesRecycleView

    @Inject
    lateinit var customProgress: CustomProgress
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NoteFramgnetBinding.bind(view)
        if (myViewModel.resourcesLoading == null || myViewModel.resourcesLoading == true)
            binding.folderShimmer.isVisible = true
        setUpRecycleView()
        getSemester()
        binding.root.setOnRefreshListener {
            resourcesRecycleView.refresh()
        }
        binding.resourcesRetry.setOnClickListener {
            resourcesRecycleView.retry()
        }
    }

    private fun checkResourcesIsEmpty(semester: String) {
        myViewModel.checkResourcesEmptyOrNot(semester).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    binding.folderShimmer.isVisible = false
                    binding.folderRecycleView.isVisible = false
                    binding.isEmptyOrNotBoss.isVisible = true
                    binding.resourceErrorText.isVisible = true
                    binding.resourceErrorText.text = "${it.exception?.localizedMessage}"
                    dialog(message = "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> Log.i(
                    TAG,
                    "checkResourcesIsEmpty: Cheking Semester is Present Or Not"
                )
                is MySealed.Success -> {
                    binding.folderShimmer.isVisible = false
                    it.data?.let {
                        binding.isEmptyOrNotBoss.isVisible = it
                        if (it) {
                            binding.folderRecycleView.isVisible = false
                            binding.isEmptyOrNotBoss.setBackgroundResource(R.drawable.filenotfound)
                            binding.resourceErrorText.isVisible = it
                            binding.resourceErrorText.text =
                                "Sorry For Inconvenience I'm Try To find Ppt and Other Resouces" +
                                        " As Soon As will Find it,I will Update the Rescouce Shortly."
                        } else
                            Log.i(TAG, "checkResourcesIsEmpty: Semeter is Present")
                    }
                }
            }
        }
    }

    private fun getSetUp() {
        lifecycleScope.launch {
            resourcesRecycleView.loadStateFlow.collectLatest {
                when (it.append) {
                    is LoadState.NotLoading -> {
                        putUIItem(flag = false, true)
                        Log.i(TAG, "getsetUpUI: Not Loading")
                    }
                    LoadState.Loading -> {
                        putUIItem(flag = true)
                        Log.i(TAG, "getsetUpUI: User Is Loading")
                    }
                    is LoadState.Error -> {
                        putUIItem(false)
                        val error = (it.append as LoadState.Error).error.localizedMessage
                        if (error.equals("List is Empty", true)) {
                            binding.isEmptyOrNotBoss.isVisible = true
                            binding.resourceErrorText.isVisible = true
                            binding.resourceErrorText.text = "$error"
                            binding.resourcesRetry.isVisible = true
                            dialog(message = "$error")
                        }
                        Log.i(TAG, "setUpUI: $error")
                    }
                }
            }
        }
    }

    private fun setData() {
        myViewModel.getResources.observe(viewLifecycleOwner) {
            resourcesRecycleView.submitData(lifecycle, it)
        }
    }

    private fun putUIItem(flag: Boolean, notLoading: Boolean = false) {
        binding.root.isRefreshing = flag
        myViewModel.resourcesLoading = false
        if (!notLoading) {
            binding.folderShimmer.isVisible = false
            binding.folderRecycleView.setBackgroundColor(
                resources.getColor(
                    R.color.light_grey,
                    null
                )
            )
        }
    }

    private fun showLoading(string: String) {
        customProgress.showLoading(requireActivity(), string)
    }

    private fun hideLoading() {
        customProgress.hideLoading(requireActivity())
    }

    private fun getSemester() {
        myViewModel.userData.observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    hideLoading()
                    binding.folderShimmer.isVisible = false
                    binding.folderRecycleView.isVisible = false
                    binding.isEmptyOrNotBoss.isVisible = true
                    binding.resourceErrorText.isVisible = true
                    binding.resourceErrorText.text = "${it.exception?.localizedMessage}"
                    dialog(message = "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    showLoading("Account Detail is Loading,")
                }
                is MySealed.Success -> {
                    hideLoading()
                    it.data?.semester?.let {
                        myViewModel.getUserSemester = it
                        Log.i(
                            TAG,
                            "getSemester: MySealing Success -> ${myViewModel.getUserSemester}"
                        )
                        setData()
                        getSetUp()
                        checkResourcesIsEmpty(it)
                    }
                }
            }
        }
    }

    private fun dialog(title: String = "Error", message: String) {
        val action =
            NoteFragmentDirections.actionGlobalPasswordDialog2(title, message)//Error
        findNavController().navigate(action)
    }

    private fun setUpRecycleView() {
        binding.apply {
            folderRecycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                resourcesRecycleView = ResourcesRecycleView {
                    itemOnClick(it)
                }
                adapter = resourcesRecycleView
            }
        }
    }

    private fun itemOnClick(it: List<SubjectInfo>) {
        Log.i(TAG, "itemOnClick: $it")
    }
}
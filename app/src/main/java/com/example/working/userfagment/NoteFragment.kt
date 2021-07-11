package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.NoteFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.resource.ResourcesRecycleView
import com.example.working.recycle.unit.UnitRecycleView
import com.example.working.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

const val RESOURCES_LOAD_SIZE = 1
const val UNIT_LOAD_SIZE = 3
const val RESOURCES = "Resource"

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.note_framgnet) {
    private lateinit var binding: NoteFramgnetBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private var resourcesRecycleView: ResourcesRecycleView? = null
    private var unitRecycleView: UnitRecycleView? = null
    private val args: NoteFragmentArgs by navArgs()

    @Inject
    lateinit var customProgress: CustomProgress
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NoteFramgnetBinding.bind(view)
        if (args.title == RESOURCES) {
            loadResources()
        } else
            loadUnit()

        binding.root.setOnRefreshListener {
            resourcesRecycleView?.let {
                it.refresh()
                return@setOnRefreshListener
            }
            unitRecycleView?.let {
                it.refresh()
                return@setOnRefreshListener
            }
        }
        binding.resourcesRetry.setOnClickListener {
            resourcesRecycleView?.let {
                it.retry()
                return@setOnClickListener
            }
            unitRecycleView?.let {
                it.retry()
                return@setOnClickListener
            }
        }
    }

    private fun loadUnit() {
        if (myViewModel.subjectLoading == null || myViewModel.subjectLoading == true)
            binding.shimmerSubject.isVisible = true
        setUpUnitRecycleView()
        setUnitData()
        setUnitUI()
    }

    private fun setUnitUI() {
        lifecycleScope.launch {
            unitRecycleView?.loadStateFlow?.collectLatest {
                when (it.append) {
                    is LoadState.NotLoading -> {
                        putUIItem(flag = false, true)
                        Log.i(TAG, "setUnitUI: Not Loading")
                    }
                    is LoadState.Loading -> {
                        putUIItem(flag = true)
                        Log.i(TAG, "setUnitUI: User Is Loading")
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
                        Log.i(TAG, "setUnitUI: $error")
                    }
                }
            }
        }
    }

    private fun setUpUnitRecycleView() {
        binding.apply {
            folderRecycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                unitRecycleView = UnitRecycleView { list, id ->
                    itemUnitOnClick(list, id)
                }
                adapter = unitRecycleView
            }
        }
    }
    private fun itemUnitOnClick(list: List<FileInfo>, id: String) {
        list.forEach { it.folderPath="${getPathFile(args.path!!).last()},$id" }
        val action = NoteFragmentDirections.actionNoteFragmentToBooksFragment(
            path = args.path!!,
            subject = null,
            filename = id,
            file = FileInfoList(fileInfo = list)
        )
        findNavController().navigate(action)
    }

    private fun loadResources() {
        if (myViewModel.resourcesLoading == null || myViewModel.resourcesLoading == true)
            binding.folderShimmer.isVisible = true
        setUpRecycleView()
        getSemester()
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
                            binding.resourceErrorText.text = APPLOGY

                        } else
                            Log.i(TAG, "checkResourcesIsEmpty: Semeter is Present")
                    }
                }
            }
        }
    }

    private fun getSetUp() {
        lifecycleScope.launch {
            resourcesRecycleView?.loadStateFlow?.collectLatest {
                when (it.append) {
                    is LoadState.NotLoading -> {
                        putUIItem(flag = false, true)
                        Log.i(TAG, "getsetUpUI: Not Loading")
                    }
                    is LoadState.Loading -> {
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
            resourcesRecycleView?.submitData(lifecycle, it)
        }
    }

    private fun setUnitData() {
        if (myViewModel.loadPath == null)
            myViewModel.oldLoadPath = getPathFile(args.path!!)

        myViewModel.loadPath = getPathFile(args.path!!)
        lifecycleScope.launch {
            myViewModel.unitFlow.collectLatest {
                Log.i(TAG, "setUnitData: New List is Here")
                unitRecycleView?.submitData(it)
            }
        }
        if (myViewModel.loadPath != myViewModel.oldLoadPath) {
            Log.i(TAG, "setUnitData: Change in Path Detached so, Refrehing start")
            binding.root.isRefreshing=true
            unitRecycleView?.refresh()
            myViewModel.oldLoadPath = myViewModel.loadPath
            binding.root.isRefreshing=false
        }
    }

    private fun putUIItem(flag: Boolean, notLoading: Boolean = false) {
        binding.root.isRefreshing = flag
        if (resourcesRecycleView != null) myViewModel.resourcesLoading = false
        else myViewModel.subjectLoading = false
        if (!notLoading) {
            binding.folderShimmer.isVisible = false
            binding.shimmerSubject.isVisible = false
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
                resourcesRecycleView = ResourcesRecycleView { list, id ->
                    itemOnClick(list, id)
                }
                adapter = resourcesRecycleView
            }
        }
    }

    private fun itemOnClick(it: List<SubjectInfo>, id: String) {
        if (id.isNotBlank()) {
            val path = "${myViewModel.getUserSemester},$id"
            Log.i(TAG, "itemOnClick: PATH -> $path, list -> $it")
            val subjectList = SubjectList(subjectInfo = it)
            val action = NoteFragmentDirections.actionNoteFragmentToBooksFragment(
                path = path,
                subject = subjectList,
                filename = id,
                file = null
            )
            findNavController().navigate(action)
        }
    }
}

const val APPLOGY = "Sorry For Inconvenience I'm Try To find Resouces" +
        " As Soon As I will Found it,then i will Update Rescouce Shortly."
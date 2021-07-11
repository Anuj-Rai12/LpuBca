package com.example.working.userfagment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.R
import com.example.working.REQUEST_GAL
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.ShareFramgentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.share.ShareRecycleView
import com.example.working.utils.MySealed
import com.example.working.utils.checkGalleryPermission
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShareUserData : Fragment(R.layout.share_framgent), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ShareFramgentBinding
    private val adminViewModel: AdminViewModel by activityViewModels()

    @Inject
    lateinit var shareRecycleView: ShareRecycleView

    private val courseArrayAdapter: ArrayAdapter<String> by lazy {
        val course = resources.getStringArray(R.array.Course)
        ArrayAdapter(requireContext(), R.layout.dropdaown, course)
    }
    private val semesterAdapter: ArrayAdapter<String> by lazy {
        val weeks = resources.getStringArray(R.array.timers)
        ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
    }
    private val unitAdapter: ArrayAdapter<String> by lazy {
        val unit = resources.getStringArray(R.array.UnitName)
        ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
    }
    private var getSemester: String? = null
    private var getUnit: String? = null
    private var getMaterial: String? = null
    private val requestGal = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            adminViewModel.getGalleryFile(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ShareFramgentBinding.bind(view)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            getSemester = it.getString(Semester)
            getUnit = it.getString(Unit)
            getMaterial = it.getString(MATE)
        }
        setSemester()
        binding.shareFileEmail.setOnClickListener {
            val subject = binding.ShareNameTxT.text.toString()
            val fileName = binding.ShareFileName.text.toString()
            if (checkUI(subject)
                || getSemester == null
                || getMaterial == null || getUnit == null
                || checkUI(fileName)
                || adminViewModel.localData.value?.isEmpty() == true
                || adminViewModel.localData.value == null
            ) {
                Log.i(TAG, "onViewCreated: Subject-> $subject")
                Log.i(TAG, "onViewCreated: FileName-> $fileName")
                Log.i(TAG, "onViewCreated: Material-> $getMaterial")
                Log.i(TAG, "onViewCreated: Semester-> $getSemester")
                Log.i(TAG, "onViewCreated: Unit-> $getUnit")
                Snackbar.make(requireView(), "Please Enter the Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                //ShareFile-Multiple
                shareItem(subject, fileName)
            }
        }
        binding.shareWebsite.setOnClickListener {
            val websiteLink =
                if (checkUI(binding.shareWebsiteLink.text.toString())) null
                else binding.shareWebsiteLink.text.toString()
            val subject = binding.ShareNameTxT.text.toString()
            if (websiteLink == null || getMaterial == null || getUnit == null || getSemester == null
                || checkUI(subject)
            ) {
                Snackbar.make(requireView(), "Please Fill The Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(activity, "Website Is Completed", Toast.LENGTH_SHORT).show()
        }
        getDropDownId()
        getAdminEmail()
        setUpRecyclerview()
        setUpdateFile()
    }

    private fun setUpdateFile() {
        adminViewModel.localData.observe(viewLifecycleOwner) {
            shareRecycleView.notifyDataSetChanged()
            shareRecycleView.submitList(it)
        }
    }

    private fun setUpRecyclerview() {
        binding.apply {
            fileRecycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = shareRecycleView
            }
        }
    }

    private fun getAdminEmail() {
        adminViewModel.getAdminEmailId.observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> Log.i(TAG, "getAdminEmail: ${it.exception?.localizedMessage}")
                is MySealed.Loading -> Log.i(TAG, "getAdminEmail:  Getting Admin Loading")
                is MySealed.Success -> adminViewModel.adminEmail = it.data
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_folder, menu)
        val optionMenu = menu.findItem(R.id.addFolderBitch)
        optionMenu.setOnMenuItemClickListener {
            if (checkGalleryPermission(requireActivity())) {
                requestGal.launch("*/*")
            } else {
                request()
                Toast.makeText(activity, "Please Give Permission Storage", Toast.LENGTH_SHORT)
                    .show()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun request(
        camera: String = Manifest.permission.READ_EXTERNAL_STORAGE,
        code: Int = REQUEST_GAL,
        s: String = "Gallery"
    ) = EasyPermissions.requestPermissions(
        this,
        "Kindly Give us $s permission,otherwise application may not work Properly.",
        code,
        camera
    )

    private fun shareItem(
        subject: String,
        fileName: String,
    ) {
        Log.i(TAG, "onViewCreated: Subject-> $subject")
        Log.i(TAG, "onViewCreated: FileName-> $fileName")
        Log.i(TAG, "onViewCreated: Material-> $getMaterial")
        Log.i(TAG, "onViewCreated: Semester-> $getSemester")
        Log.i(TAG, "onViewCreated: Unit-> $getUnit")
        Log.i(TAG, "shareItem: AdminEmail ->${adminViewModel.adminEmail}")
        Log.i(TAG, "shareItem: All the Data->${adminViewModel.localData.value}")
    }

    private fun checkUI(view: String): Boolean {
        return (view.isBlank() || view.isEmpty())
    }

    private fun getDropDownId() {
        binding.apply {
            ShareUnitLayoutTxT.setOnItemClickListener { _, _, position, _ ->
                getUnit = unitAdapter.getItem(position)
            }
            materialCollection.setOnItemClickListener { _, _, position, _ ->
                getMaterial = courseArrayAdapter.getItem(position)
            }
            shareSemesterId.setOnItemClickListener { _, _, position, _ ->
                getSemester = semesterAdapter.getItem(position)
            }
        }
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val weekArray = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        val unit = resources.getStringArray(R.array.UnitName)
        val unitO = ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
        val course = resources.getStringArray(R.array.Course)
        val courseArray = ArrayAdapter(requireContext(), R.layout.dropdaown, course)
        binding.shareSemesterId.setAdapter(weekArray)
        binding.materialCollection.setAdapter(courseArray)
        binding.ShareUnitLayoutTxT.setAdapter(unitO)
    }


    override fun onResume() {
        super.onResume()
        setSemester()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getUnit?.let {
            outState.putString(Unit, it)
        }
        getSemester?.let {
            outState.putString(Semester, it)
        }
        getMaterial?.let {
            outState.putString(MATE, it)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        perms.forEach {
            if (EasyPermissions.permissionPermanentlyDenied(this, it)) {
                SettingsDialog.Builder(requireContext()).build().show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        requestGal.launch("*/*")
    }
}

data class LocalFileInfo(
    val fileUrl: Uri
)

private const val Unit = "GET_UNIT"
private const val Semester = "GET_SEMESTER"
private const val MATE = "GET_MATERIAL"
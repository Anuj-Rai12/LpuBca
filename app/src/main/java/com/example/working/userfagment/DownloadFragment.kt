package com.example.working.userfagment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.DownloadFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.DownloadRecycleView
import com.example.working.room.UserData
import com.example.working.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class DownloadFragment : Fragment(R.layout.download_framgnet) {
    private lateinit var binding: DownloadFramgnetBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private val adminViewModel: AdminViewModel by activityViewModels()
    private var downloadRecycleView: DownloadRecycleView? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFramgnetBinding.bind(view)
        setUpRecycleView()
        myViewModel.downloadUpdateFile.observe(viewLifecycleOwner) { userList ->
            setUiEmpty(userList.isEmpty())
            downloadRecycleView?.submitList(userList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUiEmpty(flag: Boolean) {
        binding.EmptyImage.isVisible = flag
        val color = if (!flag) R.color.light_grey else R.color.back_lastIcon
        binding.root.setBackgroundColor(resources.getColor(color, null))
    }
    private fun setUpRecycleView() {
        binding.apply {
            DownloadRecycle.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                downloadRecycleView = DownloadRecycleView(requireActivity()) {
                    itemOnClick(it)
                }
                adapter = downloadRecycleView
            }
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val getSelectedItem =
                        downloadRecycleView?.currentList?.get(viewHolder.absoluteAdapterPosition)
                    getSelectedItem?.let {
                        adminViewModel.deleteDownload(it)
                    }
                }
            }).attachToRecyclerView(DownloadRecycle)
        }
        lifecycleScope.launch {
            adminViewModel.taskEvent.collect { mySealedChannel ->
                when (mySealedChannel) {
                    is MySealedChannel.DeleteAndChannel<*> -> {
                        val obj=mySealedChannel.userdata as UserData
                        Snackbar.make(
                            requireView(),
                            "${obj.fileInfo.fileName} is Deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("UNDO") {
                            adminViewModel.saveDownload(userData = obj)
                                .observe(viewLifecycleOwner) { mySealed ->
                                    if (mySealed is MySealed.Success) {
                                        dialog("Success!", mySealed.data!!)
                                    }
                                }
                        }.show()
                    }
                }
            }
        }
    }

    private fun viewDocumentFile(uri: Any, fileInfo: FileInfo) {
        val trueUri = when (uri) {
            is File -> getFileUrl(uri, requireContext())
            is Uri -> uri
            else -> null
        }
        trueUri?.let { viewUri ->
            try {
                getMimeType(viewUri, requireContext())?.let { mime ->
                    Log.i(TAG, "viewDocumentFile: Type ->$mime")
                    val objIntent = Intent(Intent.ACTION_VIEW)
                    objIntent.setDataAndType(viewUri, mime)
                    objIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    objIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context?.startActivity(Intent.createChooser(objIntent, "Open Document!"))
                    myViewModel.downloadFile[fileInfo.fileName!!] = viewUri
                    return
                }
                Log.i(TAG, "viewDocumentFile: MIME IS null")
            } catch (e: ActivityNotFoundException) {
                dialog(message = "No Document Viewer Found\n\nTip: Download The Document Viewer to View this File,")
            } catch (e: Exception) {
                dialog(message = "Exception ${e.localizedMessage}")
                Log.i(TAG, "viewDocumentFile: ${e.message}")
            }
        }
        if (trueUri == null)
            dialog(message = "Uri Not Found")
    }

    private fun dialog(title: String = "Error!", message: String) {
        val action = DownloadFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    private fun dir(title: String, fileInfo: FileInfo) {
        val action = DownloadFragmentDirections.actionDownloadFragmentToViewFileFragment(
            fileinfo = fileInfo,
            title
        )
        findNavController().navigate(action)
    }

    private fun itemOnClick(fileInfo: FileInfo) {
        Log.i(TAG, "itemOnClick: $fileInfo")
        if (getFileDir(fileInfo.fileName!!, requireContext()).exists()
            || isPngFile(fileInfo.fileName) || isJpgFile(fileInfo.fileName)
        ) {
            if (isDocxFile(fileInfo.fileName) || isDocFile(fileInfo.fileName))
                viewDocumentFile(fileInfo.localDownloadUrl?.toUri()!!, fileInfo)
            else {
                dir(fileInfo.fileName, fileInfo)
            }
        } else {
            if (isDocxFile(fileInfo.fileName) || isDocFile(fileInfo.fileName))
                dialog(message = "File Not Found,\nSo Download it Again!!")
            else {
                fileInfo.localDownloadUrl = null
                dir(fileInfo.fileName, fileInfo)
            }
        }
    }
}
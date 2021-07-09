package com.example.working.userfagment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.DownloadFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.DownloadRecycleView
import com.example.working.utils.getFileUrl
import com.example.working.utils.getMimeType
import com.example.working.utils.isDocFile
import com.example.working.utils.isDocxFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DownloadFragment : Fragment(R.layout.download_framgnet) {
    private lateinit var binding: DownloadFramgnetBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private var downloadRecycleView: DownloadRecycleView? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFramgnetBinding.bind(view)
        setUpRecycleView()
        myViewModel.downloadUpdateFile.observe(viewLifecycleOwner) {
            downloadRecycleView?.submitList(it)
        }
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
        }
    }
    private fun viewDocumentFile(uri: Any, fileInfo: FileInfo) {
        val trueUri = when (uri) {
            is File -> getFileUrl(uri,requireContext())
            is Uri -> uri
            else -> null
        }
        trueUri?.let { viewUri ->
            try {
                getMimeType(viewUri, requireContext())?.let { mime ->
                    val objIntent = Intent(Intent.ACTION_VIEW)
                    objIntent.setDataAndType(viewUri, mime)
                    objIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    objIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context?.startActivity(Intent.createChooser(objIntent, "Hello"))
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

    private fun itemOnClick(fileInfo: FileInfo) {
        Log.i(TAG, "itemOnClick: $fileInfo")
        if (isDocxFile(fileInfo.fileName!!) || isDocFile(fileInfo.fileName))
            viewDocumentFile(fileInfo.localDownloadUrl?.toUri()!!, fileInfo)
        else {
            val action = DownloadFragmentDirections.actionDownloadFragmentToViewFileFragment(
                fileinfo = fileInfo,
                fileInfo.fileName
            )
            findNavController().navigate(action)
        }
    }
}
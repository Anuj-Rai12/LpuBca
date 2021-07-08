package com.example.working.userfagment

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.BookFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.file.FileRecycleView
import com.example.working.recycle.subject.SubjectRecycleView
import com.example.working.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BooksFragment : Fragment(R.layout.book_fragment) {
    private lateinit var binding: BookFragmentBinding
    private val args: BooksFragmentArgs by navArgs()
    private val myViewModel: MyViewModel by activityViewModels()
    private var subjectRecycleView: SubjectRecycleView? = null
    private var fileRecycleView: FileRecycleView? = null

    @Inject
    lateinit var customProgress: CustomProgress
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BookFragmentBinding.bind(view)
        args.subject?.let {
            setRecycleView()
            setData()
            checkItEmpty()
            return
        }
        args.file?.let {
            setUpFileRecycleView()
            setFileData(it.fileInfo)
            checkItEmpty()
            return
        }
    }

    private fun setFileData(fileInfoList: List<FileInfo>) =
        fileRecycleView?.submitList(fileInfoList)

    private fun setUpFileRecycleView() {
        binding.apply {
            recycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                fileRecycleView = FileRecycleView(requireActivity()) {file->
                    onClickFileInfo(file)
                }
                adapter = fileRecycleView
            }
        }
    }

    private fun onClickFileInfo(fileInfo: FileInfo) {
        Log.i(TAG, "onClickFileInfo: FileInfo -> $fileInfo")
        if (isDocxFile(fileInfo.fileName!!) || isDocFile(fileInfo.fileName)) {
            try {
                val uri = myViewModel.downloadFile.getValue(fileInfo.fileName)
                //viewFile
                viewDocumentFile(uri, fileInfo)
            } catch (e: NoSuchElementException) {
                //Create File
                val id = setFileDownload(fileInfo)
                setBroadcastReceiver(id, fileInfo)
            }
        } else {
            val action = BooksFragmentDirections.actionBooksFragmentToViewFileFragment(
                fileInfo,
                fileInfo.fileName
            )
            findNavController().navigate(action)
        }
    }

    private fun showLoading(message: String = "Document is Downloading,\nDon't close the App.") =
        customProgress.showLoading(requireActivity(), message)

    private fun hideLoading() = customProgress.hideLoading(requireActivity())

    private fun setFileDownload(fileInfo: FileInfo): Long {
        val uri = getFileDir(fileInfo.fileName!!, requireContext())
        val request = getDownloadRequest(fileInfo, uri)
        activity?.let {
            showLoading()
        }
        val downloadManger: DownloadManager =
            activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManger.enqueue(request)
    }

    private fun setBroadcastReceiver(TrueId: Long, fileInfo: FileInfo) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                activity?.let {
                    hideLoading()
                }
                if (id == TrueId) {
                    val uri = getFileDir(fileName = fileInfo.fileName!!, requireContext())
                    activity?.let {
                        Toast.makeText(
                            it,
                            "Downloaded Path \n ${getFileUrl(uri)}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    //Show Document file
                    viewDocumentFile(uri, fileInfo)
                    return

                } else
                    Log.i(TAG, "onReceive: File is Not Downloaded")
                    activity?.let {
                        dialog(message = "File is Not Downloaded :(")
                    }
            }
        }
        activity?.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun getFileUrl(file: File): Uri? {
        return FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName.toString() + ".provider",
            file
        )
    }

    private fun viewDocumentFile(uri: Any, fileInfo: FileInfo) {
        val trueUri= when (uri) {
            is File -> getFileUrl(uri)
            is Uri -> uri
            else -> null
        }
        trueUri?.let {viewUri->
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
        if(trueUri==null)
        dialog(message = "Uri Not Found")
    }

    private fun dialog(title: String = "Error!", message: String) {
        activity?.let {
        val action = BooksFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
        }
    }

    private fun checkItEmpty() {
        if (args.subject?.subjectInfo?.isEmpty() == true || args.file?.fileInfo?.isEmpty() == true) {
            binding.apply {
                recycleView.isVisible = false
                subjectErrorText.isVisible = true
                isEmptySubject.isVisible = true
                subjectErrorText.text = APPLOGY
            }
        }
    }

    private fun setData() = subjectRecycleView?.submitList(args.subject?.subjectInfo)

    private fun setRecycleView() {
        binding.recycleView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            subjectRecycleView = SubjectRecycleView {
                itemOnClick(it)
            }
            adapter = subjectRecycleView
        }
    }

    private fun itemOnClick(s: String) {
        Log.i(TAG, "itemOnClick: SubjectName -> $s")
        val path = "${args.path},$s"
        Log.i(TAG, "itemOnClick: SubjectName -> $path")
        val action =
            BooksFragmentDirections.actionBooksFragmentToNoteFragment(title = s, path = path)
        findNavController().navigate(action)
    }
}
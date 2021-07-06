package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.BookFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.file.FileRecycleView
import com.example.working.recycle.subject.SubjectRecycleView

class BooksFragment : Fragment(R.layout.book_fragment) {
    private lateinit var binding: BookFragmentBinding
    private val args: BooksFragmentArgs by navArgs()
    private var subjectRecycleView: SubjectRecycleView? = null
    private var fileRecycleView:FileRecycleView?=null
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

    private fun setFileData(fileInfoList: List<FileInfo>) =fileRecycleView?.submitList(fileInfoList)

    private fun setUpFileRecycleView() {
        binding.apply {
            recycleView.apply {
                setHasFixedSize(true)
                layoutManager=LinearLayoutManager(requireContext())
                fileRecycleView= FileRecycleView{
                    onClickFileInfo(it)
                }
                adapter=fileRecycleView
            }
        }
    }

    private fun onClickFileInfo(fileInfo: FileInfo) {
        Log.i(TAG, "onClickFileInfo: FileInfo -> $fileInfo")
        val action=BooksFragmentDirections.actionBooksFragmentToViewFileFragment(fileInfo,fileInfo.fileName?:"No Name")
        findNavController().navigate(action)
    }

    private fun checkItEmpty() {
        if (args.subject?.subjectInfo?.isEmpty() == true||args.file?.fileInfo?.isEmpty()==true) {
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
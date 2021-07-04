package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.working.R
import com.example.working.databinding.BookFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.recycle.subject.SubjectRecycleView

class BooksFragment : Fragment(R.layout.book_fragment) {
    private lateinit var binding: BookFragmentBinding
    private val args: BooksFragmentArgs by navArgs()
    private lateinit var subjectRecycleView: SubjectRecycleView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BookFragmentBinding.bind(view)
        setRecycleView()
        setData()
        checkItEmpty()
    }

    private fun checkItEmpty() {
        if (args.subject.subjectInfo.isEmpty())
        {
            binding.apply {
                recycleView.isVisible=false
                subjectErrorText.isVisible=true
                isEmptySubject.isVisible=true
                subjectErrorText.text= APPLOGY
            }
        }
    }

    private fun setData() = subjectRecycleView.submitList(args.subject.subjectInfo)

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
    }
}
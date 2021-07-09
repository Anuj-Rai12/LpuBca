package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
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
import dagger.hilt.android.AndroidEntryPoint

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

    private fun itemOnClick(fileInfo: FileInfo) {
        Log.i(TAG, "itemOnClick: $fileInfo")
        val action = DownloadFragmentDirections.actionDownloadFragmentToViewFileFragment(
            fileinfo = fileInfo,
            fileInfo.fileName!!
        )
        findNavController().navigate(action)
    }
}
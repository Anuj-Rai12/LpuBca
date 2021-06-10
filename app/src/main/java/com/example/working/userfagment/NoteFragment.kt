package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.NoteFramgnetBinding
import kotlin.system.exitProcess

class NoteFragment : Fragment(R.layout.note_framgnet) {
    private lateinit var binding: NoteFramgnetBinding
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/


override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = NoteFramgnetBinding.bind(view)

}
}
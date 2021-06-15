package com.example.working.utils

import android.app.Dialog
import android.os.Bundle
import androidx.navigation.fragment.navArgs


class PasswordDialog : androidx.fragment.app.DialogFragment() {
    private val args:PasswordDialogArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog =
            android.app.AlertDialog.Builder(requireContext()).setTitle(args.title)
                .setMessage(args.message)
                .setPositiveButton("ok") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
        return alterDialog.create()
    }
    //8.At End of Password you may use $ symbol or Any Special Symbol
}
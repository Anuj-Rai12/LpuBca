package com.example.working.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth


class PasswordDialog : androidx.fragment.app.DialogFragment() {
    private val args: PasswordDialogArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog = AlertDialog.Builder(requireActivity()).setTitle(args.title)
        alterDialog.setMessage(args.message)
            .setPositiveButton("ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        return alterDialog.create()
    }
    //8.At End of Password you may use $ symbol or Any Special Symbol
}

class UpdateDialog constructor(
    private val message: String,
    private val link: String?,
    private val title: String,
) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog = AlertDialog.Builder(activity)
        if (title != "LogOut!") {
            alterDialog
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Update") { _, _ ->
                    link?.let {
                        loadUrl(it)
                    }
                }.setNegativeButton("Exit") { _, _ ->
                    activity?.finish()
                }
        } else {
            alterDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    activity?.finish()
                }.setNegativeButton("No") { _, _ ->
                    dismiss()
                }
        }
        return alterDialog.create()
    }

    private fun loadUrl(s: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(s))
        startActivity(browserIntent)
    }
}
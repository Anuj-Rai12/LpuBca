package com.example.working.utils

import android.net.Uri


interface SendData {
        //fun callStart(intent: Intent?)
        fun sendData()
        fun sendGalImage(string: Uri)
        fun urlImage()
}
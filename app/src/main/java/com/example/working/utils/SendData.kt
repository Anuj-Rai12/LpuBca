package com.example.working.utils


interface SendData {
    fun sendData()
    fun sendGalImage()
    fun urlImage()
}

interface UpdateMyInfo {
    fun updateName(firstName: String, lastName: String)
    fun updateEmail(email: String, password: String, newEmail: String)
    fun updateSemester(semesterNo: String)
    fun updatePhoneNo(phoneWithCode: String, email: String, password: String)

}
package com.example.working.notfiy

import com.example.working.utils.AllMyConstant

data class Message(
    val title: String,
    val body: String,
    val image:String?,
    val icon:String=AllMyConstant.ICON_NAME,
    val color:String="#fb7268",
)
data class MessageData(
    val Key1:String?,
)

data class MainObject(
    val notification:Message,
    val data: MessageData,
    val to:String
)

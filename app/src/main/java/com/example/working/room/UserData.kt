package com.example.working.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.working.adminui.respotry.FileInfo

@Entity(tableName = "Download_File_Info")
data class UserData(
    @PrimaryKey
    @ColumnInfo(name = "Primary_Data")
    val date: String,
    @Embedded
    val fileInfo: FileInfo,
)

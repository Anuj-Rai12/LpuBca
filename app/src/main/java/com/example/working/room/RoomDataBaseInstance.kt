package com.example.working.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserData::class], version = 2, exportSchema = false)
abstract class RoomDataBaseInstance :RoomDatabase(){
    abstract fun getDao():DownloadDao
    companion object{
        const val Database_Name="MY_dATA_Name"
    }
}
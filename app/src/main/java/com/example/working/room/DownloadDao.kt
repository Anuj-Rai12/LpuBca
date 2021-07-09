package com.example.working.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Delete
    fun delete(userData: UserData)

    @Query("Select * from Download_File_Info")
    fun showAll(): Flow<List<UserData>>

    @Query(value = "delete from Download_File_Info")
    fun deleteAll()
}
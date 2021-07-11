package com.example.working.recycle.paginguser

import android.app.Application
import androidx.room.Room
import com.example.working.adminui.LOAD_SIZE
import com.example.working.repos.USERS
import com.example.working.repos.VERSION
import com.example.working.repos.VERSION_DOC
import com.example.working.room.RoomDataBaseInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val update by lazy {
        fireStore.collection(VERSION).document(VERSION_DOC)
    }
    private val adminId = "drZyYp5mMyMwjoK3mSYB4toYdhQ2"

    @Provides
    @Singleton
    fun getDataBaseInstance(app: Application) = Room.databaseBuilder(
        app,
        RoomDataBaseInstance::class.java,
        RoomDataBaseInstance.Database_Name,
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun loadAllUserDataQuery() = fireStore
        .collection(USERS)
        .limit(LOAD_SIZE.toLong())

    @Provides
    @Singleton
    @AdminQuery
    fun getAdminUserId() = fireStore.collection(USERS).document(adminId)

    @Provides
    @Singleton
    @GetUpdate
    fun getAllUpdate() = update.get()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetUpdate

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminQuery


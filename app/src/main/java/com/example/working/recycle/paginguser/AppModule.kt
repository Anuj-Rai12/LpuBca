package com.example.working.recycle.paginguser

import android.app.Application
import androidx.room.Room
import com.example.working.adminui.LOAD_SIZE
import com.example.working.repos.USERS
import com.example.working.repos.VERSION
import com.example.working.repos.VERSION_DOC
import com.example.working.room.RoomDataBaseInstance
import com.example.working.utils.AllMyConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
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
    private const val adminId = "ZFruF8WEdSbM3itFxjqlNhpR8As2"

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
    @Subscriber
    fun subscribeIt() = FirebaseMessaging.getInstance().subscribeToTopic(AllMyConstant.TOPIC)

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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Subscriber

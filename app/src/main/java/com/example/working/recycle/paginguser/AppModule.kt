package com.example.working.recycle.paginguser

import android.app.Application
import androidx.room.Room
import com.example.working.adminui.LOAD_SIZE
import com.example.working.notfiy.NotifyInterface
import com.example.working.repos.USERS
import com.example.working.repos.VERSION
import com.example.working.repos.VERSION_DOC
import com.example.working.room.RoomDataBaseInstance
import com.example.working.utils.AllMyConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.internal.GsonBuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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


    private val client by lazy {
        OkHttpClient.Builder().apply {
            readTimeout(20, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    @Singleton
    fun getRetrofit(): NotifyInterface =
        Retrofit.Builder()
            .baseUrl(AllMyConstant.URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotifyInterface::class.java)


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

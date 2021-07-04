package com.example.working.recycle.paginguser

import com.example.working.adminui.LOAD_SIZE
import com.example.working.repos.USERS
import com.example.working.repos.VERSION
import com.example.working.repos.VERSION_DOC
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

//THIS MODULE IS USE TO GET QUERY
//Error -> it Create is Query is One Time
@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val update by lazy {
        fireStore.collection(VERSION).document(VERSION_DOC)
    }

    @Provides
    fun loadAllUserDataQuery() = fireStore
        .collection(USERS)
        .limit(LOAD_SIZE.toLong())

    @Provides
    @Singleton
    @GetUpdate
    fun getAllUpdate() = update.get()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetUpdate


package com.example.working.recycle.paginguser

import com.example.working.adminui.LOAD_SIZE
import com.example.working.repos.USERS
import com.example.working.repos.VERSION
import com.example.working.repos.VERSION_DOC
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

//THIS MODULE IS USE TO GET QUERY

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val udi by lazy {
        FirebaseAuth.getInstance()
    }
    private val reference by lazy {
        udi.currentUser?.uid?.let { fireStore.collection(USERS).document(it) }
    }

    private val update by lazy {
        fireStore.collection(VERSION).document(VERSION_DOC)
    }

    @Provides
    @Singleton
    fun loadAllUserDataQuery() = fireStore
        .collection(USERS)
        .limit(LOAD_SIZE.toLong())

    @Provides
    @GETLodgedUser
    fun getLodgedUser() = reference?.get()
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
annotation class GETLodgedUser

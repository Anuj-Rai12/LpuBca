package com.example.working.recycle.paginguser

import com.example.working.adminui.LOAD_SIZE
import com.example.working.repos.USERS
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun loadAllUserDataQuery() = FirebaseFirestore.getInstance()
        .collection(USERS)
        .limit(LOAD_SIZE.toLong())
}
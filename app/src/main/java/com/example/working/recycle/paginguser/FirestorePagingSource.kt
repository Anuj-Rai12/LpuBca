package com.example.working.recycle.paginguser

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.working.utils.userchannel.FireBaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirestorePagingSource(
    private val getAllUserData: Query
) : PagingSource<QuerySnapshot, FireBaseUser>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, FireBaseUser>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FireBaseUser> {
        return try {
            val currentPage = params.key ?: getAllUserData.get().await()
            val lastDocumented = currentPage.documents.last()
            val nextPage = getAllUserData.startAfter(lastDocumented).get().await()
            val users: MutableList<FireBaseUser> = mutableListOf()
            currentPage.forEach {
                val op=it.toObject(FireBaseUser::class.java)
                op.id=it.id
                users.add(op)
            }
            LoadResult.Page(
                data = users,
                prevKey =null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
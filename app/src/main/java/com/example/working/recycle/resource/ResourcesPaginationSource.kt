package com.example.working.recycle.resource


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.working.repos.USERS
import com.example.working.utils.Materials
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.tasks.await

class ResourcesPaginationSource(private val resourcesQuery: Query) :
    PagingSource<QuerySnapshot, Materials>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Materials>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Materials> {
        return try {
            val currentPage = params.key ?: resourcesQuery.get().await()
            val lastDocumented = currentPage.documents.last()
            val nextPage = resourcesQuery.startAfter(lastDocumented).get().await()
            val users: MutableList<Materials> = mutableListOf()
            currentPage.forEach {
                val op = it.toObject(Materials::class.java)
                val data=FirebaseFirestore.getInstance().collection(USERS).document(op.udi ?: "").get().await()
                val firstname =data.getField<String?>("firstname")
                val lastname =data.getField<String?>("lastname")
                op.udi="$firstname $lastname"
                op.id = it.id
                users.add(op)
            }
            LoadResult.Page(
                data = users,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
package com.example.working.recycle.resource


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.working.utils.Materials
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
            LoadResult.Page(
                data = currentPage.toObjects(Materials::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
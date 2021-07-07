package com.example.working.recycle.unit

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.working.adminui.AllData
import com.example.working.repos.USERS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.tasks.await

class UnitPaginationSource(private val query: Query) : PagingSource<QuerySnapshot, AllData>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, AllData>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, AllData> {
        return try {
            val currentPage = params.key ?: query.get().await()
            val lastDocumented = currentPage.documents.last()
            val nextPage = query.startAfter(lastDocumented).get().await()
            val users: MutableList<AllData> = mutableListOf()
            currentPage.forEach {
                val op = it.toObject(AllData::class.java)
                op.map?.filterValues { info ->
                    val data=FirebaseFirestore.getInstance().collection(USERS).document(info.sourceId ?: "")
                        .get().await()
                    val firstname =data.getField<String?>("firstname")
                    val lastname =data.getField<String?>("lastname")
                    firstname?.let { string->
                    info.sourceId="$string $lastname"
                    }
                    return@filterValues true
                }
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
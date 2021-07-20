package com.example.working

import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.working.adminui.LOAD_SIZE
import com.example.working.recycle.paginguser.FirestorePagingSource
import com.example.working.recycle.paginguser.GetUpdate
import com.example.working.recycle.resource.ResourcesPaginationSource
import com.example.working.recycle.unit.UnitPaginationSource
import com.example.working.repos.ClassPersistence
import com.example.working.repos.MyRepository
import com.example.working.room.RoomDataBaseInstance
import com.example.working.userfagment.RESOURCES_LOAD_SIZE
import com.example.working.userfagment.UNIT_LOAD_SIZE
import com.example.working.utils.Event
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository,
    private val classPersistence: ClassPersistence,
    private val getAllUserQuery: Query,
    @GetUpdate
    private val getUpdateTask: Task<DocumentSnapshot>,
    db: RoomDataBaseInstance
) : ViewModel() {
     var OtpDialogFlag:Boolean?=null
     var OtpValidFlag:Boolean?=null
    var profileTitle: String? = null
    var profileMessage: String? = null
    var profileUserEmail: String? = null
    var profileDialogFlag: Boolean? = null
    var profileUpdateIt: Boolean? = null
    var profileEmailUpdateOrNot: Boolean? = null
    var dialogFlag: Boolean = false
    val downloadUpdateFile = db.getDao().showAll().asLiveData()
    var websiteLoading: Boolean = true
    var subjectLoading: Boolean? = null
    var resourcesLoading: Boolean? = null
    var getUserSemester: String? = null
    var msg: String? = null
    var image: Bitmap? = null
    var loading: Boolean? = null
    var loadPath: List<String>? = null
    var oldLoadPath: List<String>? = null
    private var _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event

    var downloadFile: MutableMap<String, Uri> = mutableMapOf()

    fun createUser(email: String, password: String) =
        myRepository.createUser(email, password).asLiveData()

    private fun getResourceQuery() =
        FirebaseFirestore.getInstance().collection(getUserSemester ?: "")
            .orderBy("priority", Query.Direction.DESCENDING)
            .limit(RESOURCES_LOAD_SIZE.toLong())

    private fun getUnitQuery() =
        FirebaseFirestore.getInstance().collection(loadPath?.get(0) ?: "")
            .document(loadPath?.get(1) ?: " ").collection(loadPath?.get(2) ?: "")
            .limit(UNIT_LOAD_SIZE.toLong())

    fun createAccount(icon: Bitmap, userInfo1: UserInfo1) =
        myRepository.createAcc(icon, userInfo1).asLiveData()

    fun signInAccount(email: String, password: String) =
        myRepository.signInAccount(email, password).asLiveData()

    val read = classPersistence.read.asLiveData()
    fun storeInfo(email: String, password: String, flag: Boolean) {
        updateUserInfo(email, password, flag)
    }

    fun isValidPhone(phone: String): Boolean {
        val phonetic = "^[+]?[0-9]{10,13}\$"
        val pattern = Pattern.compile(phonetic)
        return pattern.matcher(phone).matches()
    }

    private fun getAllResources() = Pager(
        PagingConfig(
            pageSize = RESOURCES_LOAD_SIZE
        )
    ) {
        ResourcesPaginationSource(getResourceQuery())
    }.flow.cachedIn(viewModelScope)

    val getResources = getAllResources().asLiveData()

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    private fun updateUserInfo(email: String, password: String, flag: Boolean) =
        viewModelScope.launch {
            classPersistence.updateInfo(email, password, flag)
            _event.value = Event("Information Saved")
        }

    val flow = Pager(
        PagingConfig(
            pageSize = LOAD_SIZE
        )
    ) {
        FirestorePagingSource(getAllUserQuery)
    }.flow.cachedIn(viewModelScope)

    val unitFlow = Pager(
        PagingConfig(
            pageSize = UNIT_LOAD_SIZE
        )
    ) {
        UnitPaginationSource(getUnitQuery())
    }.flow.cachedIn(viewModelScope)


    val getUpdate = myRepository.getUpdate(getUpdateTask).asLiveData()
    val userData = myRepository.getProfileInfo().asLiveData()
    fun passwordRestEmail(email: String) = myRepository.passwordRestEmail(email).asLiveData()
    fun updateValue(semesterNo: String, SEMESTER: String) =
        myRepository.updateValue(semesterNo, SEMESTER).asLiveData()

    fun checkResourcesEmptyOrNot(semesterNo: String) =
        myRepository.checkResourcesExitsOrNot(semesterNo).asLiveData()

    fun updateNewEmail(newEmail: String) = myRepository.updateNewEmail(newEmail).asLiveData()
}
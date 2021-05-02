package com.example.listpeople.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.listpeople.FetchCompletionHandler
import com.example.listpeople.Model.FetchError
import com.example.listpeople.Model.FetchResponse
import com.example.listpeople.Model.Person
import com.example.listpeople.adapter.RecyclerViewAdapter
import com.example.listpeople.util.showErrorDialog
import com.example.listpeople.DataSource

class PeopleViewModel() : ViewModel()  {
    private var dataSource: DataSource = DataSource()

    private var _people = MutableLiveData<Set<Person>>()
    val people:LiveData<Set<Person>>
        get() = _people

    private var _isVisible = MutableLiveData<Boolean>()
    val isVisible:LiveData<Boolean>
        get() = _isVisible

    private var _errorMessage = MutableLiveData<String>()
    val errorMessage:LiveData<String>
        get() = _errorMessage

    private var _next = MutableLiveData<String>()
    val next:LiveData<String>
        get() = _next

    private var _showEmptyListMessage = MutableLiveData<Boolean>()
    val showEmptyListMessage:LiveData<Boolean>
        get() = _showEmptyListMessage

    private var _isNotRequestOnGoing= MutableLiveData<Boolean>()
    val isNotRequestOnGoing:LiveData<Boolean>
        get() = _isNotRequestOnGoing

    private var _isRecyleViewVisible= MutableLiveData<Boolean>()
    val isRecyleViewVisible:LiveData<Boolean>
        get() = _isRecyleViewVisible

    fun init(){
        _isVisible.value = true
        fetchData()
    }
    fun setNotIsRequestOnGoing(flag:Boolean){
        _isNotRequestOnGoing.value =  flag
    }

    fun setPeopleList(list:Set<Person>){
        _people.value = list
    }

    fun setVisibility(flag: Boolean){

    }

    @Synchronized
    fun fetchData(next:String?=null){
        _isNotRequestOnGoing.value = false
        dataSource.fetch(next,completionHandler = object :FetchCompletionHandler{
            override fun invoke(fetchResponse: FetchResponse?, FetchError: FetchError?) {
                _isVisible.value = false
                fetchResponse?.let {fetchResponse->
                    if(fetchResponse.people.isEmpty()){
                        _showEmptyListMessage.value = true
                    }else{
                        _isRecyleViewVisible.value = true
                        _next.value = fetchResponse.next.orEmpty()
                        if(next == null){
                            _people.value  = fetchResponse.people.toHashSet()
                        }else{
                            _people.value = _people.value?.plus(fetchResponse.people.toHashSet())
                        }
                    }
                    _isNotRequestOnGoing.value = true
                }
                FetchError?.let {fetchError->
                    print(fetchError)
                    _errorMessage.value = fetchError.errorDescription
                }

            }
        } )
    }
}
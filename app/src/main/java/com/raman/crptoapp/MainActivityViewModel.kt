package com.raman.crptoapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raman.crptoapp.data.CyptoListItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    lateinit var retroService: RetroService

    private var data = MutableLiveData<List<CyptoListItem>>()
    private var newdata = MutableLiveData<List<CyptoListItem>>()


    init {
        retroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
    }


    fun observeData() = data
    fun observeNewData() = newdata

    suspend fun fetchCyptoList() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandlerData) {
            val response = retroService.getCyptoList()
            Log.d("abcd", " " + response.size)
            data.postValue(response)
        }
    }

    suspend fun refereshCyptoList() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandlerNextData) {
            val response = retroService.getCyptoList()
            Log.d("abcd", " " + response.size)
            newdata.postValue(response)
        }
    }

    val coroutineExceptionHandlerNextData = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
        GlobalScope.launch(Dispatchers.IO) {
            refereshCyptoList()
        }
    }

    val coroutineExceptionHandlerData = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
        GlobalScope.launch(Dispatchers.IO) {
            fetchCyptoList()
        }
    }
}
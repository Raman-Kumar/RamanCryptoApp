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

    private var cryptoItemData = MutableLiveData<CyptoListItem>()
    private var symbolFor: String? = null

    init {
        retroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
    }


    fun observeData() = data
    fun observeNewData() = newdata
    fun observeCryptoItemData() = cryptoItemData

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

    suspend fun getCyptoItemList(symbol : String) {
        symbolFor = symbol
        viewModelScope.launch(Dispatchers.IO  + coroutineItemExceptionHandlerData) {
            val response = retroService.getCyptoListItem(symbol)
            Log.d("abcde", " " + response.toString())
            if (response.symbol.equals(symbol))
            cryptoItemData.postValue(response)
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

    val coroutineItemExceptionHandlerData = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
}
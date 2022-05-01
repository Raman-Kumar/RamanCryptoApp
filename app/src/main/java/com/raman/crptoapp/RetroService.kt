package com.raman.crptoapp

import com.raman.crptoapp.data.CyptoList
import com.raman.crptoapp.data.CyptoListItem
import com.raman.crptoapp.data.test
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroService {
    @GET("tickers/24hr/")
    suspend fun getCyptoList(): CyptoList

    @GET("ticker/24hr/")
    suspend fun getCyptoListItem(@Query("symbol") symbol: String): CyptoListItem
}
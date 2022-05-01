package com.raman.crptoapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*

class CryptoItemBottomSheet(private val viewModelthis: MainActivityViewModel, private val symbol: String) : BottomSheetDialogFragment() {

    var countDownTimer2 : CountDownTimer? = null
    var stop :Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.context.setTheme(R.style.InterestSheetDialogTheme)
        return inflater.inflate(R.layout.crypto_item_bottom_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlobalScope.launch(Dispatchers.IO) {
            viewModelthis.getCyptoItemList(symbol)
        }

        val tv_baseAsset : TextView = view.findViewById(R.id.tv_baseAsset)
        val tv_quoteAsset : TextView = view.findViewById(R.id.tv_quoteAsset)
        val tv_lastPrice : TextView = view.findViewById(R.id.tv_lastPrice)
        val tv_time : TextView = view.findViewById(R.id.tv_time)
        val tv_openPrice : TextView = view.findViewById(R.id.tv_openPrice)
        val tv_lowPrice : TextView = view.findViewById(R.id.tv_lowPrice)
        val tv_highPrice : TextView = view.findViewById(R.id.tv_highPrice)
        val tv_volume : TextView = view.findViewById(R.id.tv_volume)
        val tv_bidPrice : TextView = view.findViewById(R.id.tv_bidPrice)
        val tv_askPrice : TextView = view.findViewById(R.id.tv_askPrice)

        viewModelthis.observeCryptoItemData().observe(this){
            tv_baseAsset.text = it.baseAsset.uppercase()
            tv_quoteAsset.text = "/"+it.quoteAsset.uppercase()
            tv_lastPrice.text = it.lastPrice

            try {
                val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss")
                val netDate = Date(Long.parseLong(it.at.toString()))
                tv_time.text = "At : "  + sdf.format(netDate)
            } catch (e: Exception) {
            }
            tv_openPrice.text = "Open Price : " +  it.openPrice
            tv_lowPrice.text =  "Low Price : " +  it.lowPrice
            tv_highPrice.text = "High Price : " +  it.highPrice
            tv_volume.text =    "Volume : " +  it.volume
            tv_bidPrice.text =  "Bid Price : " +  it.volume
            tv_askPrice.text =  "Ask Price : " +  it.askPrice

        }

        countDownTimer2 = object : CountDownTimer((2*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: kotlin.Long) {
            }
            override fun onFinish() {
                if (stop.not())
                GlobalScope.launch(Dispatchers.IO) {
                    viewModelthis.getCyptoItemList(symbol)
                }
                if (stop.not())
                againFinish()
            }
        }.start()

    }

    override fun dismiss() {
        super.dismiss()
        stop = true
    }

    override fun onDestroy() {
        super.onDestroy()
        stop = true
    }

    fun againFinish() {
        if (stop.not())
        countDownTimer2?.start()
    }



}
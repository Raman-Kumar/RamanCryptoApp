package com.raman.crptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.raman.crptoapp.data.CyptoListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Long.parseLong
import java.text.SimpleDateFormat

import java.time.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var listofData : MutableList<CyptoListItem>? = null
    var countDownTimer2 : CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
    }

    private fun initViewModel() {
        val viewModel  = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            viewModel.fetchCyptoList()
        }
        var recyclerView = findViewById<RecyclerView>(R.id.list) as RecyclerView

        viewModel.observeData().observe(this){
            Log.d("abcd", " $it")
            listofData = it as MutableList<CyptoListItem>

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = CustomAdapter(listofData!!,viewModel )
        }

        var swipeRefreshLayout = findViewById<RecyclerView>(R.id.swiperefresh) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener{
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.refereshCyptoList()
            }
        }

        viewModel.observeNewData().observe(this){
            swipeRefreshLayout.isRefreshing = false
            Log.d("abcd", " $it")
            listofData!!.clear()
            Log.d("abcd", listofData.toString())
            listofData!!.addAll(it)
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        countDownTimer2 = object : CountDownTimer((2*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.refereshCyptoList()
                }
                againFinish()
            }
        }.start()

    }

    fun againFinish() {
        countDownTimer2?.start()
    }

    class CustomAdapter(private val dataSet: List<CyptoListItem>, private val viewModelthis: MainActivityViewModel) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tv_baseAsset: TextView
            val tv_quoteAsset: TextView
            val tv_lastPrice : TextView
            val tv_time : TextView
            init {
                // Define click listener for the ViewHolder's View.
                tv_baseAsset = view.findViewById(R.id.tv_baseAsset)
                tv_quoteAsset = view.findViewById(R.id.tv_quoteAsset)
                tv_lastPrice = view.findViewById(R.id.tv_lastPrice)
                tv_time = view.findViewById(R.id.tv_time)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.cypto_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.tv_baseAsset .text= dataSet.get(position).baseAsset.uppercase()
            viewHolder.tv_quoteAsset .text= "/" +  dataSet.get(position).quoteAsset.uppercase()
            viewHolder.tv_lastPrice.text = dataSet.get(position).lastPrice



            val dt = Instant.ofEpochSecond(dataSet.get(position).at)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            viewHolder.itemView.setOnClickListener{

            }

            try {
                val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss")
                val netDate = Date(parseLong(dataSet.get(position).at.toString()))
//                sdf.format(netDate)
                viewHolder.tv_time.text = "At : "  + sdf.format(netDate)
            } catch (e: Exception) {
            }
//            viewHolder.tv_time.text = "At : " + dataSet.get(position).at.toString()
//            viewHolder.tv_time.visibility = View.GONE
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }
}
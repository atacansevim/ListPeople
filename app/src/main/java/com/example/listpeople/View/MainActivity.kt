package com.example.listpeople.View



import android.app.Activity
import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Contacts
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.listpeople.DataSource
import com.example.listpeople.FetchCompletionHandler
import com.example.listpeople.Model.FetchError
import com.example.listpeople.Model.FetchResponse
import com.example.listpeople.Model.Person
import com.example.listpeople.R
import com.example.listpeople.ViewModel.PeopleViewModel
import com.example.listpeople.adapter.LazyLoadScrollListener
import com.example.listpeople.adapter.RecyclerViewAdapter
import com.example.listpeople.util.*
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {
    private var dataSource: DataSource = DataSource()
    private var swipeContainer: SwipeRefreshLayout? = null
    private lateinit var recyclerView:RecyclerView
    private lateinit var textView:TextView
    private var recyclerViewAdapter : RecyclerViewAdapter? = null
    private lateinit var peopleViewModel:PeopleViewModel
    private lateinit var next: String
    private var page = 1
    private var retryCount = 0
    private var isNotRequestOnGoing = false
    private var progressBar:ProgressBar? = null
    private var lazyLoadScrollListener: LazyLoadScrollListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        peopleViewModel  = ViewModelProviders.of(this).get(PeopleViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        swipeContainer = findViewById(R.id.swiperefreshlayout_people_list_container)
        textView = findViewById(R.id.textView)
        initRecyclerView()
        peopleViewModel.init()

        lazyLoadScrollListener = object : LazyLoadScrollListener() {
            override fun onLoadMore(view: RecyclerView?) {
                peopleViewModel.fetchData(next)
            }
        }

        peopleViewModel.showEmptyListMessage.observe(this, Observer {
            it?.let {
                recyclerView.visibility = View.INVISIBLE
                textView.visibility =View.VISIBLE
               if(retryCount < 2){
                   retryCount += 1
                   showErrorDialog(this,"Error","We could not found. Please press the retry button.",positiveButtonText = "Retry",positiveListener = DialogInterface.OnClickListener { _, _ -> peopleViewModel.setNotIsRequestOnGoing(true)
                   peopleViewModel.init()})
               }else{
                   peopleViewModel.setNotIsRequestOnGoing(false)
                   showErrorDialog(this,"Error","Unable to complete your request. Please try again later.",positiveButtonText = "Close",positiveListener = DialogInterface.OnClickListener { _, _ -> })

               }
            }
        })
        peopleViewModel.people.observe(this, Observer {
            it?.let {
                retryCount = 0
                recyclerView.visibility = View.VISIBLE
                textView.visibility =View.INVISIBLE
                recyclerViewAdapter = RecyclerViewAdapter(it)
                recyclerView.adapter = recyclerViewAdapter
                recyclerViewAdapter?.notifyDataSetChanged()
                forceLoadMore()
            }
        })
        peopleViewModel.errorMessage.observe(this,androidx.lifecycle.Observer {
            it?.let {
                showErrorDialog(this@MainActivity,message = it,positiveButtonText = "OK",positiveListener = DialogInterface.OnClickListener { _, _ -> peopleViewModel.setNotIsRequestOnGoing(true)})
            }
        })

        peopleViewModel.isVisible.observe(this,androidx.lifecycle.Observer {
            it?.let {
                isVisibleProgressBar(it)
            }
        })

        peopleViewModel.next.observe(this,androidx.lifecycle.Observer {
            it?.let{
                next = it
            }
        })

        peopleViewModel.isNotRequestOnGoing.observe(this,androidx.lifecycle.Observer {
            it?.let {
                isNotRequestOnGoing = it
            }
        })

        peopleViewModel.isRecyleViewVisible.observe(this,androidx.lifecycle.Observer {
            it?.let {
                if(it)
                recyclerView.visibility = View.VISIBLE
            }
        })

        lazyLoadScrollListener?.let {recyclerView.addOnScrollListener(it) }

        swipeContainer?.setOnRefreshListener {
            peopleViewModel.setPeopleList(emptySet())
            recyclerView.visibility = View.INVISIBLE
            peopleViewModel.fetchData()
            swipeContainer?.isRefreshing = false
        }
    }

    private fun initRecyclerView(){
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }

    private fun isVisibleProgressBar(visibility: Boolean){
        if(visibility){
            progressBar?.visibility = View.VISIBLE
        }else{
            progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun forceLoadMore() {
        // This is for re-trigger the fetching data if conditions are matched
        if(isNotRequestOnGoing)
            lazyLoadScrollListener?.onScrolled(recyclerView, 0, -5)
    }
}
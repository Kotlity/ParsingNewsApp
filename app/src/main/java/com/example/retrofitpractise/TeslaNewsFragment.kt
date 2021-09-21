package com.example.retrofitpractise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitpractise.adapter.MyAdapter
import com.example.retrofitpractise.additional.Resource
import com.example.retrofitpractise.db.ArticleDatabase
import com.example.retrofitpractise.repository.NewsRepository
import com.example.retrofitpractise.viewmodel.NewsViewModel
import com.example.retrofitpractise.viewmodelFactory.NewsViewModelFactory
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_tesla_news.*


class TeslaNewsFragment : Fragment(R.layout.fragment_tesla_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var myAdapter: MyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val newsViewModelFactory = NewsViewModelFactory(newsRepository)
        viewModel = ViewModelProvider(this, newsViewModelFactory).get(NewsViewModel::class.java)
        setupRecyclerView()

        myAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_teslaNewsFragment_to_articleFragment, bundle)
        }

        viewModel.teslaNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { teslaResponse ->
                        myAdapter.differ.submitList(teslaResponse.articles.toList())
                        val totalPages = teslaResponse.totalResults / 20 + 2
                        isLastPage = viewModel.teslaNewsPage == totalPages
                        if (isLastPage) {
                            recyclerViewTeslaNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d("MyLog", "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }


    private fun hideProgressBar() {
        val progressBarTeslaNews = view?.findViewById<ProgressBar>(R.id.progressBarTeslaNews)
        progressBarTeslaNews?.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        val progressBarTeslaNews = view?.findViewById<ProgressBar>(R.id.progressBarTeslaNews)
        progressBarTeslaNews?.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { // that means we are currently scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() // first visible item position in the RecyclerView
            val visibleItemCount = layoutManager.childCount // the whole list of visible items in the RecyclerView
            val totalItemCount = layoutManager.itemCount // the WHOLE LIST of items in the RecyclerView

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20  // 20 elements on one page
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getTeslaNews("tesla")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        myAdapter = MyAdapter()
        recyclerViewTeslaNews.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@TeslaNewsFragment.scrollListener)
        }
    }
}
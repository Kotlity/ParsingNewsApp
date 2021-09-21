package com.example.retrofitpractise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitpractise.adapter.MyAdapter
import com.example.retrofitpractise.db.ArticleDatabase
import com.example.retrofitpractise.repository.NewsRepository
import com.example.retrofitpractise.viewmodel.NewsViewModel
import com.example.retrofitpractise.viewmodelFactory.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment : Fragment(R.layout.fragment_saved) {

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
            findNavController().navigate(R.id.action_savedFragment_to_articleFragment, bundle)
        }

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or  ItemTouchHelper.DOWN,  // We indicate how we will scroll our list
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // We indicate how we will swipe our list
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true // performs actions when the user scrolls the list(we dont need to use this in this project)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = myAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "You have successfully deleted article: ${article.title}", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerViewSavedNews)
        }

        viewModel.getAllArticles().observe(viewLifecycleOwner, Observer { articles ->
            myAdapter.differ.submitList(articles)
        })
    }

    private fun setupRecyclerView() {
        myAdapter = MyAdapter()
        recyclerViewSavedNews.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
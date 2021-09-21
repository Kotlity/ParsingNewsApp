package com.example.retrofitpractise

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.retrofitpractise.db.ArticleDatabase
import com.example.retrofitpractise.repository.NewsRepository
import com.example.retrofitpractise.viewmodel.NewsViewModel
import com.example.retrofitpractise.viewmodelFactory.NewsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val newsViewModelFactory = NewsViewModelFactory(newsRepository)

        viewModel = ViewModelProvider(this, newsViewModelFactory).get(NewsViewModel::class.java)
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully: ${article.title} !", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.deleteArticle(article)
                    Snackbar.make(view, "You have successfully deleted a saved news item: ${article.title} !", Snackbar.LENGTH_SHORT).show()
                }
                show()
            }
        }
    }
}
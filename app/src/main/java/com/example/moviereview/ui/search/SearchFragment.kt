package com.example.moviereview.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.moviereview.R
import com.example.moviereview.data.remote.MovieResponse
import com.example.moviereview.databinding.FragmentSearchBinding
import com.example.moviereview.ui.review.ReviewDialog
import com.example.moviereview.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter by lazy { SearchAdapter(onClickMore, showReviewDialog) }
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@SearchFragment
            viewModel = searchViewModel
            rcvSearch.adapter = searchAdapter
        }

        lifecycleScope.launch {
            callMovieList()
        }
    }

    private val onClickMore: Function1<MovieResponse.Item, Unit> = { item ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
        startActivity(intent)
    }

    private val showReviewDialog: Function1<MovieResponse.Item, Unit> = { item ->
        val dialog = ReviewDialog(item)
        dialog.show(childFragmentManager, "REVIEW_DIALOG")
    }

    private fun callMovieList() {
        binding.btnSearch.setOnClickListener {
            requireContext().hideKeyboard(binding.editTextSearch)

            if (searchViewModel.editTextSearch.value.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), getString(R.string.search_enter), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            searchViewModel.getMovieList()
            searchViewModel.movieList.observe(this, {
                searchAdapter.submitList(it)
            })
        }
    }
}
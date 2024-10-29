package com.example.campusmarketplace.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.campusmarketplace.data.Category
import com.example.campusmarketplace.util.Resource
import com.example.campusmarketplace.viewmodel.CategoryViewModel
import com.example.campusmarketplace.viewmodel.retail.BaseCategoryViewModelFactoryFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ClotheFragment: BaseCategoryFragment() {
    @Inject
    lateinit var database: DatabaseReference
    @Inject
    lateinit var auth: FirebaseAuth

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactoryFactory(database, Category.Clothes, auth)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideBestProductsLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductsPagingRequest() {

    }

    override fun onOfferPagingRequest() {

    }
}
package com.example.campusmarketplace.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusmarketplace.R
import com.example.campusmarketplace.adapters.FavouriteProductAdapter
import com.example.campusmarketplace.databinding.FragmentFavouriteBinding
import com.example.campusmarketplace.util.Resource
import com.example.campusmarketplace.util.VerticalItemDecoration
import com.example.campusmarketplace.viewmodel.FavouriteViewModel
import kotlinx.coroutines.flow.collectLatest

class FavouriteFragment: Fragment(R.layout.fragment_favourite) {
    private lateinit var binding: FragmentFavouriteBinding
    private val cartAdapter by lazy { FavouriteProductAdapter() }
    private val viewModel by activityViewModels<FavouriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_favouriteFragment_to_productDetailsFragment, b)
        }

        cartAdapter.onDeleteClick = { cartProduct->
            viewModel.emitDeleteDialog(cartProduct)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest { cartProduct ->
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from favorites")
                    setMessage("Do you want to delete this item from your favorites?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        // Call the deleteFavoriteProduct function in the ViewModel
                        viewModel.deleteFavoriteProduct(cartProduct)
                        dialog.dismiss()
                    }
                }
                alertDialog.create().show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
package com.example.campusmarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusmarketplace.data.Category
import com.example.campusmarketplace.data.Product
import com.example.campusmarketplace.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val database: DatabaseReference,
    private val category: Category,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        database.child("Users").child(auth.currentUser?.uid!!).child("Products").orderByChild("category").equalTo(category.category).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val offerProductsList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val products = snapshot.getValue(Product::class.java)
                    products?.let { offerProductsList.add(it) }
                }
                _offerProducts.value = Resource.Success(offerProductsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _offerProducts.value = Resource.Error(databaseError.message)
            }
        })
    }

    fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        database.child("Users").child(auth.currentUser?.uid!!).child("Products").orderByChild("category").equalTo(category.category).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bestProductsList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val products = snapshot.getValue(Product::class.java)
                    products?.let { bestProductsList.add(it) }
                }
                _bestProducts.value = Resource.Success(bestProductsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _bestProducts.value = Resource.Error(databaseError.message)
            }
        })
    }

}
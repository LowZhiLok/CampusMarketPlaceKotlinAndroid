package com.example.campusmarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusmarketplace.data.Product
import com.example.campusmarketplace.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        database.child("Users").child(auth.currentUser?.uid!!).child("Products").orderByChild("category").equalTo("Furniture").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val specialProductsList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.key != null) {
                        val product = snapshot.getValue(Product::class.java)
                        product?.let {
                            specialProductsList.add(it)
                        }
                    }
                }
                _specialProducts.value = Resource.Success(specialProductsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _specialProducts.value = Resource.Error(databaseError.message)
            }
        })
    }


    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }

        database.child("Users").child(auth.currentUser?.uid!!).child("Products").orderByChild("category").equalTo("Electronics").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bestDealsProducts = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.key != null) {
                        val product = snapshot.getValue(Product::class.java)
                        product?.let {
                            bestDealsProducts.add(it)
                        }
                    }
                }
                _bestDealsProducts.value = Resource.Success(bestDealsProducts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _bestDealsProducts.value = Resource.Error(databaseError.message)
            }
        })
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())

                database.child("Users").child(auth.currentUser?.uid!!).child("Products").orderByKey()
                    .startAt(pagingInfo.bestProductsPage.toString())
                    .limitToFirst(10)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val bestProducts = mutableListOf<Product>()

                            for (productSnapshot in snapshot.children) {
                                val product = productSnapshot.getValue(Product::class.java)
                                product?.let {
                                    bestProducts.add(it)
                                }
                            }

                            // Update paging info
                            pagingInfo.isPagingEnd = bestProducts.size < 10
                            pagingInfo.bestProductsPage++

                            _bestProducts.value = Resource.Success(bestProducts)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _bestProducts.value = Resource.Error(error.message)
                        }
                    })
            }
        }
    }
}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)
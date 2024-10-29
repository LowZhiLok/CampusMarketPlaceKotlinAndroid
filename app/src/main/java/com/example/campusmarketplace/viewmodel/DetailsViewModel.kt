package com.example.campusmarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusmarketplace.data.CartProduct
import com.example.campusmarketplace.firebase.FirebaseCommon
import com.example.campusmarketplace.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }

        val userCartRef = database.child("Users").child(auth.currentUser?.uid!!).child("favourite")
        userCartRef.orderByChild("product/id").equalTo(cartProduct.product.id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach { snapshot ->
                        val product = snapshot.getValue(CartProduct::class.java)
                        if (product != null && product.product == cartProduct.product) {
                            viewModelScope.launch { _addToCart.emit(Resource.Success(cartProduct)) }
                            return
                        }
                    }
                }

                // If product not found or does not match, add new product
                addNewProduct(cartProduct)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                viewModelScope.launch { _addToCart.emit(Resource.Error(databaseError.message)) }
            }
        })
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}
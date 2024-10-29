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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()


    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

//    private var cartProductDocuments = emptyList<DocumentSnapshot>()
//
//
//    fun deleteCartProduct(cartProduct: CartProduct) {
//        val index = cartProducts.value.data?.indexOf(cartProduct)
//        if (index != null && index != -1) {
//            val documentId = cartProductDocuments[index].id
//            firestore.collection("user").document(auth.uid!!).collection("cart")
//                .document(documentId).delete()
//        }
//    }


    init {
        getCartProducts()
    }


    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }

        val userCartRef = database.child("Users").child(auth.currentUser?.uid!!).child("favourite")

        userCartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartProducts = mutableListOf<CartProduct>()
                for (productSnapshot in snapshot.children) {
                    val cartProduct = productSnapshot.getValue(CartProduct::class.java)
                    val key = productSnapshot.key

                    cartProduct?.let {
                        it.key = key
                        cartProducts.add(it)
                    }
                }
                viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
            }

            override fun onCancelled(error: DatabaseError) {
                viewModelScope.launch { _cartProducts.emit(Resource.Error(error.message)) }
            }
        })
    }

    fun deleteFavoriteProduct(cartProduct: CartProduct) {
        firebaseCommon.deleteFavoriteProduct(cartProduct) { deletedProduct, exception ->
            if (exception != null) {
                // Handle error
            } else {
                // Update UI or notify the fragment
            }
        }
    }

    // Expose a method to emit values to the deleteDialog SharedFlow
    fun emitDeleteDialog(cartProduct: CartProduct) {
        viewModelScope.launch {
            _deleteDialog.emit(cartProduct)
        }
    }

}
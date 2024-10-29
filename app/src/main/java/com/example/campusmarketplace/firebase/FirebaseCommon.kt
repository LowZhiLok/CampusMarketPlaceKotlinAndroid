package com.example.campusmarketplace.firebase

import com.example.campusmarketplace.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseCommon(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth
) {

    // Reference to the root node of the Realtime Database
    private val rootNode = FirebaseDatabase.getInstance().reference

    // Reference to the user's cart node
    private val userCartRef = rootNode.child("Users").child(auth.currentUser?.uid!!).child("favourite")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        // Push a new child node to generate a unique key for each cart item
        val newCartItemRef = userCartRef.push()

        // Set the value of the new child node to the cartProduct
        newCartItemRef.setValue(cartProduct)
            .addOnSuccessListener {
                // On success, return the added cartProduct and null exception
                onResult(cartProduct, null)
            }.addOnFailureListener {
                // On failure, return null for cartProduct and the exception
                onResult(null, it)
            }
    }

    fun deleteFavoriteProduct(cartProduct: CartProduct, onComplete: (Boolean, Exception?) -> Unit) {
        val favoriteRef = userCartRef.child(cartProduct.key!!)
        favoriteRef.removeValue()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener {
                onComplete(false, it)
            }
    }

}
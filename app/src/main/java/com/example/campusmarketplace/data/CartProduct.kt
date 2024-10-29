package com.example.campusmarketplace.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    var key: String? = null
): Parcelable {
    constructor() : this(Product(), "")
}
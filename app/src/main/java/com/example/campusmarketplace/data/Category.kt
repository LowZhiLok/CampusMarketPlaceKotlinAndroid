package com.example.campusmarketplace.data

sealed class Category(val category: String) {
    object Furniture: Category("Furniture")
    object Electronics: Category("Electronics")
    object Clothes: Category("Clothes")
    object SecondHands: Category("Second Hands")
}
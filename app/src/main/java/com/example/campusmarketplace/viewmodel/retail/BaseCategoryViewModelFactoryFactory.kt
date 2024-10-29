package com.example.campusmarketplace.viewmodel.retail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.campusmarketplace.data.Category
import com.example.campusmarketplace.viewmodel.CategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class BaseCategoryViewModelFactoryFactory(
    private val database: DatabaseReference,
    private val category: Category,
    private val auth: FirebaseAuth
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(database,category, auth) as T
    }
}
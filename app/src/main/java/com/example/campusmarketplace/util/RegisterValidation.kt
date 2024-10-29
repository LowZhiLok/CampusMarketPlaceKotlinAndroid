package com.example.campusmarketplace.util

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}
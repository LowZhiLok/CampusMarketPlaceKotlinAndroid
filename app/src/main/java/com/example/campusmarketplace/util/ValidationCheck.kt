package com.example.campusmarketplace.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation{
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}
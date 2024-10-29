package com.example.campusmarketplace.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusmarketplace.CmApplication
import com.example.campusmarketplace.data.User
import com.example.campusmarketplace.util.RegisterValidation
import com.example.campusmarketplace.util.Resource
import com.example.campusmarketplace.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        val userCartRef = database.child("Users").child(auth.currentUser?.uid!!)

        userCartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is triggered once when the listener is attached and again every time the data changes.
                val user = snapshot.getValue(User::class.java)

                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                viewModelScope.launch {
                    _user.emit(Resource.Error(error.message))
                }
            }
        })
    }

    fun updateUser(userId: String, username: String, mobileNumber: String, imagePath: Uri?) {
        viewModelScope.launch {
            _updateInfo.value = Resource.Loading()
            try {
                val userRef = database.child("Users").child(userId)
                val userUpdates = mutableMapOf<String, Any?>()
                userUpdates["username"] = username
                userUpdates["mobileNum"] = mobileNumber

                // Update only the specific fields
                userRef.updateChildren(userUpdates)

                // Handle image upload if needed
                if (imagePath != null) {
                    val downloadUrl = uploadImageAndUpdateUser(userId, imagePath, mobileNumber)
                    // Update user information with the image URL
                    userRef.child("imagePath").setValue(downloadUrl)
                }
                _updateInfo.value = Resource.Success(User())
            } catch (e: Exception) {
                _updateInfo.value = Resource.Error(e.message ?: "An unknown error occurred")
            }
        }
    }




    private suspend fun uploadImageAndUpdateUser(userId: String, imagePath: Uri, mobileNumber: String): String {
        try {
            // Convert Uri to Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(
                getApplication<CmApplication>().contentResolver,
                imagePath
            )

            // Convert Bitmap to byte array
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()

            // Create a unique filename for the image
            val filename = "profile_${UUID.randomUUID()}.jpg"

            // Reference to the image path in Firebase Storage
            val storageRef = storage.child("profile_images/$userId/$filename")

            // Upload the image to Firebase Storage
            val uploadTask = storageRef.putBytes(data)
            uploadTask.await() // Wait for the upload to complete

            // Get the download URL for the uploaded image
            val downloadUrl = storageRef.downloadUrl.await().toString()

            return downloadUrl // Return the download URL
        } catch (e: Exception) {
            throw e // Rethrow the exception
        }
    }

}
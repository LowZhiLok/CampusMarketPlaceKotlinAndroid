package com.example.campusmarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusmarketplace.data.User
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
class ProfileViewModel @Inject constructor(
    private val database: DatabaseReference,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        val userRef = database.child("Users").child(auth.currentUser?.uid!!)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(user))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(databaseError.message))
                }
            }
        }

        userRef.addListenerForSingleValueEvent(valueEventListener)
    }

    fun logout(){
        auth.signOut()
    }

}
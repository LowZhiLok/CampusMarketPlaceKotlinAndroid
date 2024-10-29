package com.example.campusmarketplace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.campusmarketplace.R
import com.example.campusmarketplace.data.User
import com.example.campusmarketplace.databinding.SignupTabFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupTabFragment : Fragment() {
    private lateinit var binding : SignupTabFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.signup_tab_fragment, container, false) as ViewGroup

        binding = SignupTabFragmentBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.signUpButton.setOnClickListener {
            val email = binding.emailET.text.toString()
            val username = binding.userNameET.text.toString()
            val mobileNum = binding.mobileNumET.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassET.text.toString()

            if (email.isNotEmpty() && username.isNotEmpty() && mobileNum.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, confirmPass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = User(firebaseAuth.currentUser!!.uid, email, username, mobileNum)

//                                // Create a HashMap to store user data
//                                val userData = hashMapOf(
//                                    "uid" to user.uid,
//                                    "email" to user.email,
//                                    "username" to user.username,
//                                    "mobileNum" to user.mobileNum,
//                                    "imagePath" to user.imagePath
//                                )

                                database.child(firebaseAuth.currentUser!!.uid).setValue(user)
                                    .addOnSuccessListener {
                                        binding.emailET.text.clear()
                                        binding.userNameET.text.clear()
                                        binding.mobileNumET.text.clear()
                                        binding.passET.text.clear()
                                        binding.confirmPassET.text.clear()

                                        Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(requireContext(), "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
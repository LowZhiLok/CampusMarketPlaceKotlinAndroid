package com.example.campusmarketplace.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.campusmarketplace.R
import com.example.campusmarketplace.activities.ForgotPasswordActivity
import com.example.campusmarketplace.activities.ShoppingActivity
import com.example.campusmarketplace.databinding.LoginTabFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginTabFragment : Fragment() {
    private lateinit var binding : LoginTabFragmentBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_tab_fragment, container, false) as ViewGroup

        binding = LoginTabFragmentBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.forgetPass.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), ForgotPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailET.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        requireActivity().startActivity(Intent(requireActivity(), ShoppingActivity::class.java))
                    }else {
                        Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

        val email = root.findViewById<View>(R.id.emailET)
        val pass = root.findViewById<View>(R.id.passET)
        val forgetPass = root.findViewById<View>(R.id.forget_pass)
        val login = root.findViewById<View>(R.id.loginButton)

        email.translationX = 800f
        pass.translationX = 800f
        forgetPass.translationX = 800f
        login.translationX = 800f

        val v = 0f

        email.alpha = v
        pass.alpha = v
        forgetPass.alpha = v
        login.alpha = v

        email.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(300).start()
        pass.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
        forgetPass.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
        login.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(700).start()

        return binding.root
    }
}

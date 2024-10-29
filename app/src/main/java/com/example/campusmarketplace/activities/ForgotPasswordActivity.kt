package com.example.campusmarketplace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.campusmarketplace.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val submit = findViewById<Button>(R.id.submitButton)
        val back = findViewById<Button>(R.id.backButton)

        back.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginAndRegisterActivity::class.java))
        }

        submit.setOnClickListener{
            val email = email.text.toString().trim {it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity, "Please enter email address", Toast.LENGTH_SHORT).show()
            }else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Email sent successfully to reset your password", Toast.LENGTH_LONG).show()
                        finish()
                    }else {
                        Toast.makeText(this@ForgotPasswordActivity, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
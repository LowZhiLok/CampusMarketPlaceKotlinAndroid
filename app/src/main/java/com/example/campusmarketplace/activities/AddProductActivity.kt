package com.example.campusmarketplace.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.campusmarketplace.R
import com.example.campusmarketplace.data.Product
import com.example.campusmarketplace.data.User
import com.example.campusmarketplace.databinding.ActivityAddProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAddProductBinding.inflate(layoutInflater)
    }

    private var selectedImages = mutableListOf<Uri>()
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val category = resources.getStringArray(R.array.Category)
        categorySpinner = binding.categorySpinner
        if (categorySpinner != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, category)
            categorySpinner.adapter = adapter
        }

        val selectImageActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Check if multiple images are selected
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        selectedImages.add(imageUri)
                    }
                } else {
                    // Only one image selected
                    val imageUri = data?.data
                    imageUri?.let { selectedImages.add(it) }
                }
                updateImages() // Update the UI after selecting images
            }
        }

        binding.addImagesIcon.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            val productValidation = validateInformation()

            if (!productValidation) {
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
            }else {
                lifecycleScope.launch {
                    saveProduct()
                }
            }
        }

        binding.linearBack.setOnClickListener {
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateImages() {
        val totalImages = selectedImages.size.toString()
        binding.addedTotalImages.text = "Added $totalImages images"
    }

    private suspend fun saveProduct() {
        val categorySelectedItem = categorySpinner.selectedItem.toString()
        val name = binding.nameET?.text?.toString()?.trim() ?: ""
        val price = binding.priceET?.text?.toString()?.trim() ?: ""
        val description = binding.descriptionET?.text?.toString()?.trim() ?: ""
        val images = mutableListOf<String>()

        firebaseAuth = FirebaseAuth.getInstance()

        // Firebase Storage reference
        val storageRef = Firebase.storage.reference.child("products")

        // Loop through selected images
        selectedImages.forEachIndexed { index, imageUri ->
            // Generate unique ID for each image
            val imageId = UUID.randomUUID().toString()

            // Create reference for each image
            val imageRef = storageRef.child("images/$imageId")

            // Upload image to Firebase Storage
            val uploadTask = imageRef.putFile(imageUri)

            // Listen for upload success/failure
            uploadTask.addOnSuccessListener { _ ->
                // Get download URL of uploaded image
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Add image URL to the list
                    images.add(imageUrl.toString())

                    // If all images are uploaded, save product to Realtime Database
                    if (index == selectedImages.size - 1) {
                        val product = Product(
                            UUID.randomUUID().toString(),
                            firebaseAuth.currentUser?.uid!!,
                            name,
                            categorySelectedItem,
                            price.toFloatOrNull() ?: 0.0f,
                            if (description.isEmpty()) null else description,
                            images
                        )

                        val database = Firebase.database.getReference("Users")

                        database.child(firebaseAuth.currentUser?.uid!!).child("Products").push().setValue(product)
                            .addOnSuccessListener {
                                binding.nameET?.text?.clear()
                                binding.priceET?.text?.clear()
                                binding.descriptionET?.text?.clear()
                                // Clear selected images after successful submission
                                selectedImages.clear()
                                updateImages() // Update UI to reflect cleared images

                                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Added Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle upload failure
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInformation(): Boolean {
        return !(binding.nameET?.text?.toString()?.trim().isNullOrEmpty()
                && binding.priceET?.text?.toString()?.trim().isNullOrEmpty()
                && selectedImages.isEmpty())
    }
}
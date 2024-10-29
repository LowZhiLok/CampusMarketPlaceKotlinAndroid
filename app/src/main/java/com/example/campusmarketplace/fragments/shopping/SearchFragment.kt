package com.example.campusmarketplace.fragments.shopping

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.campusmarketplace.R
import com.example.campusmarketplace.adapters.SearchAdapter
import com.example.campusmarketplace.data.Product
import com.example.campusmarketplace.databinding.FragmentSearchBinding
import com.example.campusmarketplace.util.showBottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var dataList: ArrayList<Product>
    private lateinit var adapter: SearchAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        firebaseAuth = FirebaseAuth.getInstance()

        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        dataList = ArrayList()
        adapter = SearchAdapter(requireContext(), dataList)
        binding.recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        eventListener = databaseReference!!.child(firebaseAuth.currentUser!!.uid).child("Products")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    for (itemSnapshot in snapshot.children) {
                        val dataClass = itemSnapshot.getValue(Product::class.java)
                        if (dataClass != null) {
                            dataList.add(dataClass)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    private fun searchList(text: String) {
        val searchList = ArrayList<Product>()
        for (dataClass in dataList) {
            val nameMatch = dataClass.name?.lowercase()?.contains(text.lowercase()) ?: false
            val categoryMatch = dataClass.category?.lowercase()?.contains(text.lowercase()) ?: false
            if (nameMatch || categoryMatch) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventListener?.let {
            databaseReference?.removeEventListener(it)
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}

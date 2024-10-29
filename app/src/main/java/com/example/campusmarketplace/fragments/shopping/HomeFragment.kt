package com.example.campusmarketplace.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.campusmarketplace.R
import com.example.campusmarketplace.adapters.HomeViewpagerAdapter
import com.example.campusmarketplace.databinding.FragmentHomeBinding
import com.example.campusmarketplace.fragments.categories.ClotheFragment
import com.example.campusmarketplace.fragments.categories.ElectronicFragment
import com.example.campusmarketplace.fragments.categories.FurnitureFragment
import com.example.campusmarketplace.fragments.categories.MainCategoryFragment
import com.example.campusmarketplace.fragments.categories.SecondHandFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            FurnitureFragment(),
            ElectronicFragment(),
            ClotheFragment(),
            SecondHandFragment()
        )

        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome){tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Furniture"
                2 -> tab.text = "Electronics"
                3 -> tab.text = "Clothes"
                4 -> tab.text = "Second Hands"
            }
        }.attach()
    }
}
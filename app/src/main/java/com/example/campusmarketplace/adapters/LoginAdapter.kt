package com.example.campusmarketplace.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.campusmarketplace.fragments.LoginTabFragment
import com.example.campusmarketplace.fragments.SignupTabFragment

class LoginAdapter(
    fm: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fm, lifeCycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            LoginTabFragment()
        }else {
            SignupTabFragment()
        }
    }

}

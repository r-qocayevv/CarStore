package com.carstore.app.ui.fragments.main

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.carstore.app.R
import com.carstore.app.adapters.AllCarsAdapter
import com.carstore.app.databinding.FragmentHomeBinding
import com.carstore.app.models.Car
import com.carstore.app.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding : FragmentHomeBinding? = null
    val binding get() = _binding!!
    private var adapter = AllCarsAdapter()
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.backgroundColor)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavBar)?.visibility = View.VISIBLE
        binding.rv.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rv.adapter = adapter

        homeViewModel.showAllPost()

        homeViewModel.allCarPost.observe(viewLifecycleOwner){ allCarPostList ->
            adapter.setData(allCarPostList)
        }
        homeViewModel.documentIdAndCarPostHashMap.observe(viewLifecycleOwner){hashMap ->
            adapter.documentIdAndCarPost = hashMap
        }

        binding.sidebarMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.sidebarNavView.bringToFront()

        binding.sidebarNavView.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sell_car -> findNavController().navigate(R.id.action_homeFragment_to_sellCarFragment)
            R.id.logout -> homeViewModel.logout(requireActivity(),requireContext())
            else -> return false
        }
        return true
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
package com.carstore.app.ui.fragments.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.carstore.app.R
import com.carstore.app.adapters.AllCarsAdapter
import com.carstore.app.databinding.FragmentHomeBinding
import com.carstore.app.ui.activities.AuthActivity
import com.carstore.app.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    private var adapter = AllCarsAdapter()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    private val homeViewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.backgroundColor)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavBar)?.visibility = View.VISIBLE
        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rv.adapter = adapter

        homeViewModel.showAllPost(firestore)

        homeViewModel.allCarPost.observe(viewLifecycleOwner) { allCarPostList ->
            adapter.setData(allCarPostList)
        }
        homeViewModel.postIdAndCarPostHashMap.observe(viewLifecycleOwner) { hashMap ->
            adapter.postIdAndCarPost = hashMap
        }

        binding.sidebarMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.sidebarNavView.bringToFront()

        binding.sidebarNavView.setNavigationItemSelectedListener(this)

    }

    fun logout() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Do you want to log out?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
        alertDialog.setNegativeButton("No") { _, _ ->
        }
        alertDialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sell_car -> findNavController().navigate(R.id.action_homeFragment_to_sellCarFragment)
            R.id.logout -> logout()
            else -> return false
        }
        return true
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
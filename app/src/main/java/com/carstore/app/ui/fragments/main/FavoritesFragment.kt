package com.carstore.app.ui.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.carstore.app.R
import com.carstore.app.adapters.FavoritesAdapter
import com.carstore.app.databinding.FragmentFavoritesBinding
import com.carstore.app.viewmodel.FavoritesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore


class FavoritesFragment : Fragment() {
    private var _binding : FragmentFavoritesBinding? = null
    val binding get() = _binding!!
    private val firestore = Firebase.firestore
    private val db = Firebase.database.reference
    private val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
    private var likedPostsID = listOf<String>()
    private val favoritesViewModel : FavoritesViewModel by viewModels()
    private val adapter = FavoritesAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.backgroundColor)
        binding.rv.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rv.adapter = adapter

        favoritesViewModel.getLikedPostID(db,currentUserUID!!)

        favoritesViewModel.postIDList.observe(viewLifecycleOwner) {
            likedPostsID = it
            favoritesViewModel.getMapFromFirestore(firestore,likedPostsID)
        }

        favoritesViewModel.likedPostListIsEmpty.observe(viewLifecycleOwner) { likedPostIsEmpty ->
            if (likedPostIsEmpty){
                binding.noLikedPostImageView.visibility = View.VISIBLE
                binding.noLikedPostText.visibility = View.VISIBLE
            }else {
                binding.noLikedPostImageView.visibility = View.INVISIBLE
                binding.noLikedPostText.visibility = View.INVISIBLE
            }
        }

        favoritesViewModel.carsDetailMap.observe(viewLifecycleOwner) {
            adapter.setData(it,likedPostsID)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
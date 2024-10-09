package com.carstore.app.ui.fragments.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.R
import com.carstore.app.adapters.CarDetailImageAdapter
import com.carstore.app.databinding.FragmentCarDetailsBinding
import com.carstore.app.models.Car
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class CarDetailsFragment : Fragment() {
    private var _binding : FragmentCarDetailsBinding? = null
    val binding get() = _binding!!
    private var carDetails : Car? = null
    private var documentID : String = ""
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCarDetailsBinding.inflate(inflater,container,false)
        activity?.window?.findViewById<BottomNavigationView>(R.id.bottomNavBar)?.visibility = View.INVISIBLE
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.orange)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rv.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        arguments?.let {
            carDetails = CarDetailsFragmentArgs.fromBundle(it).carDetails
            documentID = CarDetailsFragmentArgs.fromBundle(it).documentID
        }
        carDetails?.let {

            if (auth.currentUser?.uid != it.uidOfSharingUser ){
                binding.deleteImage.visibility = View.GONE
            }
            binding.conditionText.text = if (it.new) { " New" }else { " Used" }
            binding.brandAndModelText.text = "${it.brand}, ${it.model}"
            binding.locationText.text = it.location
            binding.priceText.text = it.price.toString()
            binding.yearText.text = " "+it.year.toString()
            binding.descriptionText.text = it.description
            binding.rv.adapter = CarDetailImageAdapter(it.image)

        }
        binding.backImage.setOnClickListener {
            findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
        }

        binding.deleteImage.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Are you sure you want to delete?")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                firestore.collection("posts").document(documentID).delete().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
                findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
            }
            alertDialog.setNegativeButton("No") { _, _ ->
            }
            alertDialog.show()
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
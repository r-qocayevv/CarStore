package com.carstore.app.ui.fragments.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carstore.app.R
import com.carstore.app.adapters.CarDetailImageAdapter
import com.carstore.app.databinding.FragmentCarDetailsBinding
import com.carstore.app.models.Car
import com.carstore.app.viewmodel.CarDetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore


class CarDetailsFragment : Fragment() {
    private var _binding: FragmentCarDetailsBinding? = null
    val binding get() = _binding!!
    private var carDetails: Car? = null
    private var postID: String = ""
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    private val db = Firebase.database.reference
    private val carDetailsViewModel: CarDetailsViewModel by viewModels()
    private var likeStatus : Boolean = false
    private var phoneNumber : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailsBinding.inflate(inflater, container, false)
        activity?.apply {
            window?.findViewById<BottomNavigationView>(R.id.bottomNavBar)?.visibility =
                View.INVISIBLE
            window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.orange)
            onBackPressedDispatcher.addCallback(viewLifecycleOwner){
                findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
            }
        }


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            carDetails = CarDetailsFragmentArgs.fromBundle(it).carDetails
            postID = CarDetailsFragmentArgs.fromBundle(it).postID
        }
        carDetails?.let {
            carDetailsViewModel.getUserPhoneNumber(it.uidOfSharingUser,db)

            if (auth.currentUser?.uid != it.uidOfSharingUser) {
                binding.deleteImage.visibility = View.GONE
            }
            binding.conditionText.text = if (it.new) {
                " New"
            } else {
                " Used"
            }
            binding.brandAndModelText.text = "${it.brand}, ${it.model}"
            binding.locationText.text = it.location
            binding.priceText.text = it.price.toString()
            binding.yearText.text = " " + it.year.toString()
            binding.descriptionText.text = it.description
            binding.viewPager.adapter = CarDetailImageAdapter(it.image)

        }

        carDetailsViewModel.phoneNumber.observe(viewLifecycleOwner){
            phoneNumber = it
            binding.phoneNumberText.text = phoneNumber
        }

        carDetailsViewModel.loadingLikeStatus.observe(viewLifecycleOwner) { likeStatusIsLoading ->
            if (likeStatusIsLoading) {
                binding.heartImage.visibility = View.INVISIBLE
            } else {
                binding.heartImage.visibility = View.VISIBLE
            }
        }

        binding.backImage.setOnClickListener {
            findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
        }

        carDetailsViewModel.getLikeStatus(db, auth.currentUser!!.uid, postID)
        carDetailsViewModel.likeStatus.observe(viewLifecycleOwner) {
            likeStatus = it
            if (likeStatus) {
                binding.heartImage.setImageResource(R.drawable.ic_liked)
            } else {
                binding.heartImage.setImageResource(R.drawable.ic_unliked)
            }
        }

        binding.callNowBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:"+phoneNumber))
            startActivity(intent)
        }

        binding.heartImage.setOnClickListener {
            carDetailsViewModel.deleteOrAddFavorite(likeStatus, db, auth, postID)
            carDetailsViewModel.getLikeStatus(db, auth.currentUser!!.uid, postID)
        }

        binding.deleteImage.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Are you sure you want to delete?")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                firestore.collection("posts").document(postID).delete().addOnSuccessListener {
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
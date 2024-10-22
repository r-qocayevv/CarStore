package com.carstore.app.ui.fragments.main


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.R
import com.carstore.app.adapters.AdImagesAdapter
import com.carstore.app.databinding.FragmentSellCarBinding
import com.carstore.app.models.Car
import com.carstore.app.viewmodel.SellCarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellCarFragment : Fragment() {
    private var _binding : FragmentSellCarBinding? = null
    val binding get() = _binding!!
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private val sellCarViewModel : SellCarViewModel by viewModels()
    private  var adImagesAdapter = AdImagesAdapter()
    private var adImagesUriList  = ArrayList<String>()
    private var selectedBrand : String? = null
    private var selectedModel : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSellCarBinding.inflate(inflater, container, false)
        registerLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.apply {
            findViewById<BottomNavigationView>(R.id.bottomNavBar)?.visibility = View.INVISIBLE
            window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.orange)
            onBackPressedDispatcher.addCallback(viewLifecycleOwner){
                findNavController().navigate(R.id.action_sellCarFragment_to_homeFragment)
            }
        }
        binding.imagesRecyclerView.adapter = adImagesAdapter
        binding.imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)

        sellCarViewModel.getBrands()
        sellCarViewModel.brandList.observe(viewLifecycleOwner){
            val brandArrayAdapter = ArrayAdapter(requireContext(),R.layout.custom_spinner_item,it)
            binding.brandSpinner.adapter = brandArrayAdapter
            binding.brandSpinner.onItemSelectedListener = object  : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sellCarViewModel.getModels(position)
                    selectedBrand = it[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    println("Nothing selected")
                }

            }
        }
        sellCarViewModel.modelList.observe(viewLifecycleOwner){
            val modelArrayAdapter = ArrayAdapter(requireContext(),R.layout.custom_spinner_item,it)
            binding.modelSpinner.adapter = modelArrayAdapter
            binding.modelSpinner.onItemSelectedListener = object : OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedModel = it[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }

        binding.shareBtn.setOnClickListener {viewFromBtn ->

            val selectedRadioButtonID = binding.radioGroup.checkedRadioButtonId
            val radioButtonText = binding.radioGroup.findViewById<RadioButton>(selectedRadioButtonID).text.toString()
            val isCarNew = sellCarViewModel.isCarNew(radioButtonText)
            val year = binding.yearET.text.toString()
            val location = binding.locationET.text.toString()
            val price = binding.priceET.text.toString()
            val description = binding.descriptionET.text.toString()
            if (selectedModel.isNullOrEmpty() || price.isEmpty() || year.isEmpty() || selectedBrand.isNullOrEmpty() || location.isEmpty() || description.isEmpty() ||adImagesAdapter.itemCount == 0){
                Snackbar.make(viewFromBtn,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
            }else {
                sellCarViewModel.uploadImagesToFirebaseStorage(adImagesUriList)
                sellCarViewModel.imageFirebaseUrlList.observe(viewLifecycleOwner){
                    sellCarViewModel.currentsUserUID?.let { currentUserUID ->
                        val carInfo = Car(currentUserUID,selectedModel!!,price.toInt(),year.toInt(),selectedBrand!!,location,description,it,isCarNew)
                        sellCarViewModel.uploadCarInfoToFirestore(carInfo,viewFromBtn)
                    }


                }

            }

        }


        binding.uploadImage.setOnClickListener {
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                        Snackbar.make(it,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("OK"){
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                    }else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }else {
                    intentToGallery()
                }
            }else{
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(it,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("OK"){
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    }else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }else {
                    intentToGallery()
                }
            }
        }
    }

    private fun intentToGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.setAction(Intent.ACTION_PICK)
        activityResultLauncher.launch(intent)
    }

    private fun registerLauncher (){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                try {
                    val intentFromResult = result.data
                    if (intentFromResult?.data != null) {
                     convertUriToBitmap(intentFromResult.data!!)

                    } else if (intentFromResult?.clipData != null) {
                        val clipData = intentFromResult.clipData
                        for (index in 0..<clipData!!.itemCount) {
                            val item: ClipData.Item = clipData.getItemAt(index)
                            convertUriToBitmap(item.uri)
                        }
                    }else {
                        Toast.makeText(requireContext(),"Image not selected",Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(),e.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                val intent = Intent()
                intent.setType("image/*")
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intent.setAction(Intent.ACTION_PICK)
                activityResultLauncher.launch(intent)
            }else {
                Toast.makeText(requireContext(),"Permission needed for gallery!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertUriToBitmap(uri : Uri) : Bitmap{
        addImages(uri)
        val bitmap = if (VERSION.SDK_INT >= VERSION_CODES.P){
            val source = ImageDecoder.createSource(requireContext().contentResolver,uri)
            ImageDecoder.decodeBitmap(source)
        }else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)
        }
        return bitmap
    }

    private fun addImages (uri: Uri) {
        adImagesUriList.add(uri.toString())
        adImagesAdapter.setData(adImagesUriList)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
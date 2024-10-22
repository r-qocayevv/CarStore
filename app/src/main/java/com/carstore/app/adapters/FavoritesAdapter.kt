package com.carstore.app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.databinding.CarsRowLayoutBinding
import com.carstore.app.models.Car
import com.carstore.app.ui.fragments.main.FavoritesFragmentDirections
import com.carstore.app.util.Constants.Companion.BRAND_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.DESCRIPTION_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.IMAGE_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.IS_NEW
import com.carstore.app.util.Constants.Companion.LOCATION_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.MODEL_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.PRICE_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.UID_OF_SHARING_USER
import com.carstore.app.util.Constants.Companion.YEAR_STORAGE_NAME
import com.carstore.app.util.MyDiffUtilClass
import com.squareup.picasso.Picasso

class FavoritesAdapter  : RecyclerView.Adapter<FavoritesAdapter.Holder>() {
    private var likedCarPost = emptyList<Map<String,Any>>()
    private var likedPostsID = emptyList<String>(

    )
    inner class Holder(val binding: CarsRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(CarsRowLayoutBinding.inflate(layoutInflater,parent,false))
    }

    override fun getItemCount(): Int {
        return likedCarPost.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        val currentCarPost = convertMapToCarClass(likedCarPost[position])
        Picasso.get().load(currentCarPost.image[0]).into(binding.carImage)
        binding.carModel.text = currentCarPost.model
        binding.carPrice.text = currentCarPost.price.toString()

        binding.cardView.setOnClickListener { viewFromButton ->
            println(likedPostsID[position]+" "+currentCarPost)
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToCarDetailsFragment(likedPostsID[position],currentCarPost)
            Navigation.findNavController(viewFromButton).navigate(action)
        }
    }

    fun setData(newLikedCarPost: List<Map<String,Any>>, newLikedPostsID : List<String>) {
        val diffUtil = MyDiffUtilClass(likedCarPost,newLikedCarPost)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        likedCarPost = newLikedCarPost.toList()
        likedPostsID = newLikedPostsID.toList()
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun convertMapToCarClass (map : Map<String,Any>) : Car {
        val uidOfSharingUser = map[UID_OF_SHARING_USER] as String
        val model = map[MODEL_STORAGE_NAME] as String
        val brand = map[BRAND_STORAGE_NAME] as String
        val description = map[DESCRIPTION_STORAGE_NAME] as String
        val location = map[LOCATION_STORAGE_NAME] as String
        val new = map[IS_NEW] as Boolean
        val price = map[PRICE_STORAGE_NAME] as Long
        val year = map[YEAR_STORAGE_NAME] as Long
        val image = map[IMAGE_STORAGE_NAME] as List<String>

        return Car(uidOfSharingUser,model,price.toInt(),year.toInt(),brand,location,description,image,new)
    }


}
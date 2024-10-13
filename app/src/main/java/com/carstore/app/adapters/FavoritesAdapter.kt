package com.carstore.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.databinding.CarsRowLayoutBinding
import com.carstore.app.models.Car
import com.carstore.app.ui.fragments.main.FavoritesFragmentDirections
import com.carstore.app.util.MyDiffUtilClass
import com.squareup.picasso.Picasso

class FavoritesAdapter() : RecyclerView.Adapter<FavoritesAdapter.Holder>() {
    var likedCarPost = emptyList<Map<String,Any>>()
    var likedPostsID = emptyList<String>(

    )
    inner class Holder(val binding: CarsRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(CarsRowLayoutBinding.inflate(layoutInflater,parent,false))
    }

    override fun getItemCount(): Int {
        return likedCarPost.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        val currentCarPost = convertMapToCarClass(likedCarPost[position])
        Picasso.get().load(currentCarPost.image.get(0)).into(binding.carImage)
        binding.carModel.text = currentCarPost.model
        binding.carPrice.text = currentCarPost.price.toString()

        binding.cardView.setOnClickListener { viewFromButton ->
            println(likedPostsID[position]+" "+currentCarPost)
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToCarDetailsFragment(likedPostsID[position],currentCarPost)
            Navigation.findNavController(viewFromButton).navigate(action)
        }
    }

    fun setData(newlikedCarPost: List<Map<String,Any>>, newLikedPostsID : List<String>) {
        val diffUtil = MyDiffUtilClass(likedCarPost,newlikedCarPost)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        likedCarPost = newlikedCarPost.toList()
        likedPostsID = newLikedPostsID.toList()
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun convertMapToCarClass (map : Map<String,Any>) : Car {
        val uidOfSharingUser = map["uidOfSharingUser"] as String
        val model = map["model"] as String
        val brand = map["brand"] as String
        val description = map["description"] as String
        val location = map["location"] as String
        val new = map["new"] as Boolean
        val price = map["price"] as Long
        val year = map["year"] as Long
        val image = map["image"] as List<String>

        return Car(uidOfSharingUser,model,price.toInt(),year.toInt(),brand,location,description,image,new)
    }


}
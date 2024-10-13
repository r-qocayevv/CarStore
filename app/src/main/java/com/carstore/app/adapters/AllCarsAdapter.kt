package com.carstore.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.databinding.CarsRowLayoutBinding
import com.carstore.app.models.Car
import com.carstore.app.ui.fragments.main.HomeFragmentDirections
import com.carstore.app.util.MyDiffUtilClass
import com.squareup.picasso.Picasso

class AllCarsAdapter() : RecyclerView.Adapter<AllCarsAdapter.Holder>() {
    var carList: List<Car> = emptyList()
    var postIdAndCarPost = HashMap<Car,String>()

    inner class Holder(val binding: CarsRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater =LayoutInflater.from(parent.context)
        return Holder(CarsRowLayoutBinding.inflate(inflater,parent,false))
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentCarPost  = carList[position]
        val binding = holder.binding
        Picasso.get().load(currentCarPost.image[0]).into(binding.carImage)
        binding.carModel.text = currentCarPost.model
        binding.carPrice.text = currentCarPost.price.toString()

        binding.cardView.setOnClickListener { viewFromButton ->
            val documentID = postIdAndCarPost[currentCarPost]
            if (documentID != null){
                val action = HomeFragmentDirections.actionHomeFragmentToCarDetailsFragment(documentID,currentCarPost,)
                Navigation.findNavController(viewFromButton).navigate(action)
            }

        }

    }

    fun setData (newData : List<Car>) {
        val diffUtil = MyDiffUtilClass(carList,newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        carList = newData.toList()
        diffUtilResult.dispatchUpdatesTo(this)

    }
}
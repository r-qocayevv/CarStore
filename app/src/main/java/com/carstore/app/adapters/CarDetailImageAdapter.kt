package com.carstore.app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.databinding.CarDetailImagesAdapterLayoutBinding
import com.squareup.picasso.Picasso

class CarDetailImageAdapter (private val carImages : List<String>) : RecyclerView.Adapter<CarDetailImageAdapter.Holder>() {


    class Holder(val binding : CarDetailImagesAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(CarDetailImagesAdapterLayoutBinding.inflate(layoutInflater,parent,false))
    }

    override fun getItemCount(): Int {
        return carImages.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        val currentImage = carImages[position]
        Picasso.get().load(Uri.parse(currentImage)).into(binding.imageView)
    }
}
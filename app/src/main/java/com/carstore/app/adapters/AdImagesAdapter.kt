package com.carstore.app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.carstore.app.databinding.AdImagesLayoutBinding
import com.carstore.app.util.MyDiffUtilClass

class AdImagesAdapter ()  : RecyclerView.Adapter<AdImagesAdapter.Holder>(){

    var carImagesList = emptyList<String>()

    class Holder (var binding : AdImagesLayoutBinding) : RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(AdImagesLayoutBinding.inflate(layoutInflater,parent,false))
    }

    override fun getItemCount(): Int {
        return carImagesList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        binding.imageView2.setImageURI(Uri.parse(carImagesList[position]))
    }

    fun setData (newCarImages : List<String>){
        val diffUtil = MyDiffUtilClass(carImagesList, newCarImages)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        carImagesList = newCarImages.toList()
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
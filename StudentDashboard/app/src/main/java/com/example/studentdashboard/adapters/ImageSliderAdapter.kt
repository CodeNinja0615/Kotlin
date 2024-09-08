package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ItemImageSliderBinding
import com.example.studentdashboard.models.SchoolImages

open class ImageSliderAdapter(
    private val context: Context,
    private val list: ArrayList<SchoolImages>,
) : RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
        val textOverlay = binding.textOverlay
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.user_place_holder_black)
            .into(holder.imageView)
        holder.textOverlay.text = model.title

    }

    override fun getItemCount(): Int = list.size
}
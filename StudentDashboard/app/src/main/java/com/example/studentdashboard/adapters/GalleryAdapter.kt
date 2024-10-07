package com.example.studentdashboard.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ItemGalleryImageBinding
import java.util.Random

open class GalleryAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
) : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    private val random = Random()

    inner class MyViewHolder(val binding: ItemGalleryImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivEventImage = binding.ivEventImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageList = list[position]

        // Randomly set one of the three predefined heights for the image view
        val randomHeight = getRandomHeight()
        val layoutParams = holder.ivEventImage.layoutParams
        layoutParams.height = randomHeight
        holder.ivEventImage.layoutParams = layoutParams

        // Load the image using Glide
        Glide
            .with(context)
            .load(imageList)
            .centerCrop()
            .placeholder(R.drawable.ic_result_placeholder)
            .into(holder.ivEventImage)

        holder.itemView.setOnClickListener {
            showFullImageDialog(imageList)
        }
    }

    private fun showFullImageDialog(imageUrl: String) {
        // Create a dialog with full screen
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_result_fullimage)

        // Reference the ImageView in the dialog
        val imageView: ImageView = dialog.findViewById(R.id.iv_full_image)

        // Load the image into the ImageView using Glide
        Glide
            .with(context)
            .load(imageUrl)
            .fitCenter()
            .placeholder(R.drawable.ic_result_placeholder)
            .into(imageView)

        imageView.setOnClickListener {
            dialog.dismiss()
        }
        // Show the dialog
        dialog.show()
    }

    override fun getItemCount(): Int = list.size

    // Randomly returns one of three predefined heights (small, medium, large)
    private fun getRandomHeight(): Int {
        // Define three height variations (in pixels)
        val smallHeight = 300  // Small height (300px)
        val mediumHeight = 450  // Medium height (450px)
        val largeHeight = 600   // Large height (600px)

        // Randomly choose one of the three sizes
        return when (random.nextInt(3)) {
            0 -> smallHeight
            1 -> mediumHeight
            else -> largeHeight
        }
    }
}

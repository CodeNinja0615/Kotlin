package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdashboard.databinding.LibraryItemBinding
import com.example.studentdashboard.models.Library

open class LibraryAdapter(
    private val context: Context,
    private val list: ArrayList<Library>,
) : RecyclerView.Adapter<LibraryAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: LibraryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvPublisher = binding.tvPublisher
        val tvBookDescription = binding.tvBookDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LibraryItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        holder.tvTitle.text = model.title
        holder.tvPublisher.text = model.publisher
        holder.tvBookDescription.text = model.description

    }

    override fun getItemCount(): Int = list.size
}
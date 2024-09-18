package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ItemStudentsBinding
import com.example.studentdashboard.models.User


open class StudentAdapter(
    private val context: Context,
    private val list: ArrayList<User>,
) : RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemStudentsBinding) : RecyclerView.ViewHolder(binding.root) {
        val cardStudent = binding.cardStudent
        val civStudentImage = binding.civStudentImage
        val tvStudentName = binding.tvStudentName
        val tvStudentEmail = binding.tvStudentEmail
        val tvStudentMobile = binding.tvStudentMobile
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStudentsBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (model.tag == "student") {
            holder.cardStudent.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_result_placeholder)
                .into(holder.civStudentImage)

            holder.tvStudentName.text = model.name
            holder.tvStudentEmail.text = model.email
            holder.tvStudentMobile.text = model.mobile.toString()
        }
    }

    override fun getItemCount(): Int = list.size
}
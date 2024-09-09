package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ClassNoticeItemBinding
import com.example.studentdashboard.databinding.MarksItemBinding
import com.example.studentdashboard.models.Marks
import com.example.studentdashboard.models.Notice
import com.example.studentdashboard.models.User


open class MarksAdapter(
    private val context: Context,
    private val list: ArrayList<Marks>,
) : RecyclerView.Adapter<MarksAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: MarksItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTermTitle = binding.tvTermTitle
        val ivResultImage = binding.ivResultImage
        val tvPercentage = binding.tvPercentage
        val tvCgpa = binding.tvCgpa
        val tvGrade = binding.tvGrade
        val tvStatus = binding.tvStatus
        val tvViewFullImage = binding.tvViewFullImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MarksItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.tvTermTitle.text = model.term
        holder.tvPercentage.text = "${model.percentage}%"
        holder.tvCgpa.text = model.cgpa.toString()
        holder.tvGrade.text = model.grade
        holder.tvStatus.text = model.status

        // Set the status text color based on pass/fail
        if (model.status.equals("Pass", ignoreCase = true)) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        Glide
            .with(context)
            .load(model.imageResult)
            .centerCrop()
            .placeholder(R.drawable.ic_result_placeholder)
            .into(holder.ivResultImage)

    }

    override fun getItemCount(): Int = list.size
}
package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdashboard.databinding.ClassNoticeItemBinding
import com.example.studentdashboard.models.Notice

open class ClassNoticeAdapter(
    private val context: Context,
    private val list: ArrayList<Notice>,
) : RecyclerView.Adapter<ClassNoticeAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ClassNoticeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvCreatedBy = binding.tvCreatedBy
        val tvNotice = binding.tvNotice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ClassNoticeItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        holder.tvTitle.text = model.title
        holder.tvCreatedBy.text = model.createdBy
        holder.tvNotice.text = model.notice

    }

    override fun getItemCount(): Int = list.size
}
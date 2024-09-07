package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdashboard.databinding.NoticeItemBinding

open class SchoolNoticeAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
) : RecyclerView.Adapter<SchoolNoticeAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: NoticeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNotice = binding.tvNotice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NoticeItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notice = list[position]
        holder.tvNotice.text = notice

    }

    override fun getItemCount(): Int = list.size
}
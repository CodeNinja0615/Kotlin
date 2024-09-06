package com.example.studentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdashboard.databinding.AbsenceItemBinding
import com.example.studentdashboard.models.User

open class AbsenceAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
) : RecyclerView.Adapter<AbsenceAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: AbsenceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivBoardImage = binding.tvAbsenceDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AbsenceItemBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val absenceList = list[position]
        holder.ivBoardImage.text = absenceList

    }

    override fun getItemCount(): Int = list.size
}
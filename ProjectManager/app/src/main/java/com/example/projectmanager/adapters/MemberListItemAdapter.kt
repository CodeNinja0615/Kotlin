package com.example.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ItemMemberBinding
import com.example.projectmanager.models.Card
import com.example.projectmanager.models.User

open class MemberListItemAdapter(
    private val context: Context,
    private val list: ArrayList<User>,
) : RecyclerView.Adapter<MemberListItemAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvMemberName = binding.tvMemberName
        val tvMemberEmail = binding.tvMemberEmail
        val ivMemberImage = binding.ivMemberImage
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.user_place_holder_black)
                .into(holder.ivMemberImage)
            holder.tvMemberName.text = model.name
            holder.tvMemberEmail.text = model.email
        }

    }

    interface OnClickListener{
        fun onClick(position: Int, model: Card)
    }

    override fun getItemCount(): Int = list.size
}
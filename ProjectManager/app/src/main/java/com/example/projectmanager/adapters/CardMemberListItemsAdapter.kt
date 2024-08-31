package com.example.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ItemCardSelectedMemberBinding
import com.example.projectmanager.databinding.ItemMemberBinding
import com.example.projectmanager.models.SelectedMembers
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants

open class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
) : RecyclerView.Adapter<CardMemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(val binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivAddMember = binding.ivAddMember
        val ivSelectedMemberImage = binding.ivSelectedMemberImage
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            if (position == list.size - 1){
                holder.ivAddMember.visibility = View.VISIBLE
                holder.ivSelectedMemberImage.visibility = View.GONE
            }else{
                holder.ivAddMember.visibility = View.GONE
                holder.ivSelectedMemberImage.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.user_place_holder_black)
                    .into(holder.ivSelectedMemberImage)
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }

    }

    interface OnClickListener{
        fun onClick()
    }

    override fun getItemCount(): Int = list.size
}
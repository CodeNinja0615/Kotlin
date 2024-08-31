package com.example.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ItemMemberBinding
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<User>,
) : RecyclerView.Adapter<MemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvMemberName = binding.tvMemberName
        val tvMemberEmail = binding.tvMemberEmail
        val ivMemberImage = binding.ivMemberImage
        val ivSelectedMember = binding.ivSelectedMember
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
            if (model.selected){
                holder.ivSelectedMember.visibility = View.VISIBLE
            }else{
                holder.ivSelectedMember.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    if (model.selected){
                        onClickListener!!.onClick(position,model, Constants.UNSELECT)
                    }else{
                        onClickListener!!.onClick(position,model, Constants.SELECT)
                    }
                }
            }
        }

    }

    interface OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }

    override fun getItemCount(): Int = list.size
}
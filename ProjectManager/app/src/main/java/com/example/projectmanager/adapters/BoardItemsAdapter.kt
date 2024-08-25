package com.example.projectmanager.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ItemBoardBinding
import com.example.projectmanager.models.Board

open class BoardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Board>,
) : RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivBoardImage = binding.ivBoardImage
        val tvBoardName = binding.tvBoardName
        val tvCreatedBy = binding.tvCreatedBy
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
//            holder.ivBoardImage.setImageURI(Uri.parse(model.image))//---Using Glide instead
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.user_place_holder_black)
                .into(holder.ivBoardImage)
            holder.tvBoardName.text = model.name
            holder.tvCreatedBy.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }

    }

    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int = list.size
}
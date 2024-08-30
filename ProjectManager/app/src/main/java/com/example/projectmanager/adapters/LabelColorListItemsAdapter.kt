package com.example.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanager.databinding.ItemLabelColorBinding
import java.util.*

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<LabelColorListItemsAdapter.MyViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root) {
        val viewMain = binding.viewMain
        val ivSelectedColor = binding.ivSelectedColor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLabelColorBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        return MyViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        if (holder is MyViewHolder) {

            holder.viewMain.setBackgroundColor(Color.parseColor(item))

            if (item == mSelectedColor) {
                holder.ivSelectedColor.visibility = View.VISIBLE
            } else {
                holder.ivSelectedColor.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {

                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }



    interface OnItemClickListener {

        fun onClick(position: Int, color: String)
    }
}
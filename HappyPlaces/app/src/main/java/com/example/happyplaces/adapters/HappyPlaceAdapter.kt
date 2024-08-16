package com.example.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ItemHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(
    private val list: ArrayList<HappyPlaceModel>,
) : RecyclerView.Adapter<HappyPlaceAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null//-------For detail page

    inner class MyViewHolder(val binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivPlaceImage = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    //------------Below function will be used to access the interface below
    fun setOnClickListener(onClickListener: OnClickListener){//-------For detail page
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
//            holder.binding.apply {
        holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
        holder.tvTitle.text = model.title
        holder.tvDescription.text = model.description

//        }

        holder.itemView.setOnClickListener {//-------For detail page
            if (onClickListener != null){
                onClickListener!!.onClick(position, model)
            }
        }
    }

    interface OnClickListener{ //-------For detail page
        fun onClick(position: Int, model: HappyPlaceModel)
    }


    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int, context: Context){ //-------For Swipe to Edit
        val intent = Intent(context, AddHappyPlaceActivity::class.java) //---Navigating to Add Screen
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position]) //----Taking the data while navigating
        activity.startActivityForResult(intent, requestCode) // To get to "onActivityResult" in "MainActivity" with request code and changed value
        notifyItemChanged(position) //--------To refresh the adapter after item change
    }

    fun removeAt(position: Int, context: Context){ //-------For Swipe to Delete
        val dbHandler = DatabaseHandler(context)
        val isDelete = dbHandler.deleteHappyPlace(list[position])
        if (isDelete>0){
            list.removeAt(position)
            notifyItemRemoved(position)//--------To refresh the adapter after item change
        }
    }

    override fun getItemCount(): Int = list.size
}

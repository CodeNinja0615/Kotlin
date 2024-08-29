package com.example.projectmanager.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanager.activities.TaskListActivity
import com.example.projectmanager.databinding.ItemTaskBinding
import com.example.projectmanager.models.Task

open class TaskListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Task>,):
    RecyclerView.Adapter<TaskListItemsAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvAddTaskList = binding.tvAddTaskList
        val llTaskItem= binding.llTaskItem
        val tvTaskListTitle = binding.tvTaskListTitle
        val cvAddTaskListName = binding.cvAddTaskListName
        val ibCloseListName = binding.ibCloseListName
        val ibDoneListName = binding.ibDoneListName
        val etTaskListName = binding.etTaskListName
        val tvAddCard = binding.tvAddCard
        val ibEditListName = binding.ibEditListName
        val ibDeleteList = binding.ibDeleteList
        val etEditTaskListName = binding.etEditTaskListName
        val ibCloseEditableView = binding.ibCloseEditableView
        val ibDoneEditListName = binding.ibDoneEditListName
        val cvEditTaskListName = binding.cvEditTaskListName
        val llTitleView = binding.llTitleView
        val rvCardList = binding.rvCardList
        val cvAddCard = binding.cvAddCard
        val ibCloseCardName = binding.ibCloseCardName
        val etCardName = binding.etCardName
        val ibDoneCardName = binding.ibDoneCardName
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(/*parent.context*/context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(
            (15.toDp().toPx())
            ,0
            , (40.toDp().toPx())
            ,0)
        binding.root.layoutParams = layoutParams
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            if (position == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            }else{
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
                holder.tvAddCard.visibility = View.VISIBLE
            }

            holder.tvTaskListTitle.text = model.title

            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }
            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }
            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
//                        context.createTaskList(position,listName)
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "Can not create task with no name", Toast.LENGTH_LONG).show()
                }
            }

            holder.ibEditListName.setOnClickListener {
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }
            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }

            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()
                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context, "Can not edit task with no name", Toast.LENGTH_LONG).show()
                }
            }

            holder.ibDeleteList.setOnClickListener {
                alertDialogToDeleteTask(position, model.title)
            }

            holder.tvAddCard.setOnClickListener {
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
//                holder.etCardName.visibility = View.VISIBLE
            }

            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()
                if (cardName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                }else{
                    Toast.makeText(context, "Can not create card with no name", Toast.LENGTH_LONG).show()
                }
            }

            holder.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.rvCardList.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context, model.cards) //------card list adapter
            holder.rvCardList.adapter = adapter

            adapter.setOnClickListener(
                object: CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if (context is TaskListActivity){
                            context.cardDetails(position, cardPosition)
                        }
                    }
                }
            )

        }
    }



    private fun alertDialogToDeleteTask(position: Int, title: String){
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(context)

        // Set the title and message
        builder.setTitle("Delete $title")
//        builder.setIcon(R.drawable.ic_alert_icon)

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }

        // Set the negative button (No)
        builder.setNegativeButton("No") { dialogInterface, _ ->
            // You can add more logic here if needed
            dialogInterface.dismiss()
        }


        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun getItemCount(): Int = list.size


    private fun Int.toDp():Int = (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx():Int = (this*Resources.getSystem().displayMetrics.density).toInt()

}
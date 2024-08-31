package com.example.projectmanager.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanager.adapters.MemberListItemsAdapter
import com.example.projectmanager.databinding.DialogListBinding
import com.example.projectmanager.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MemberListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val binding: DialogListBinding = DialogListBinding.inflate(layoutInflater) // Initialize binding

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(binding)
    }

    private fun setUpRecyclerView(binding: DialogListBinding) {
        binding.tvTitle.text = title

        if (list.size > 0) {

            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemsAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnClickListener(object : MemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}
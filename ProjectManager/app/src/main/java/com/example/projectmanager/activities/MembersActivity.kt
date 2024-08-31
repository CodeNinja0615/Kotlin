package com.example.projectmanager.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanager.R
import com.example.projectmanager.adapters.MemberListItemsAdapter
import com.example.projectmanager.databinding.ActivityMembersBinding
import com.example.projectmanager.databinding.DialogSearchMemberBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants

class MembersActivity : BaseActivity() {
    private var binding: ActivityMembersBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        showProgressDialog("Please wait....")
        FireStoreClass().getAssignedMemberListDetails(this, mBoardDetails.assignedTo)
    }

    fun setUpMembersList(list: ArrayList<User>){
        mAssignedMembersList = list //----To initialize the value right after entering the Activity
        hideProgressDialog()
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, list)
        binding?.rvMembersList?.adapter = adapter

    }


    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id) //-------putting the new found user id in board model
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user) //----------------updating the Board in firestore with the new value
    }



    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        val dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog("Please wait....")
                FireStoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(this, "type an email to search for member", Toast.LENGTH_LONG).show()
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setUpMembersList(mAssignedMembersList) //--------To pass on the updated value to recycler view

    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //------For three dot on the right side of TopBar
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddMember->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = resources.getString(R.string.members)
            binding?.toolbarMembersActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarMembersActivity?.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.STATUS_BAR_HIDDEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
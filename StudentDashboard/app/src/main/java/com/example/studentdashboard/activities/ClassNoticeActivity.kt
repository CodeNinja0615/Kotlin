package com.example.studentdashboard.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.ClassNoticeAdapter
import com.example.studentdashboard.databinding.ActivityClassNoticeBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants

class ClassNoticeActivity : BaseActivity() {
    private var binding: ActivityClassNoticeBinding? = null
    private lateinit var mUserDetails: User
    private lateinit var mClassRoom: ClassRoom
    private val SET_NEW_NOTICE = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassNoticeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        if (intent.hasExtra(Constants.USERS)){
            mUserDetails = intent.getParcelableExtra(Constants.USERS)!!
        }

        if (mUserDetails.tag == "teacher"){
            binding?.fabCreateNotice?.visibility = View.VISIBLE
        }

        showProgressDialog("Please wait....")
        FireStoreClass().loadClassRoomData(this, mUserDetails.grade)

        binding?.fabCreateNotice?.setOnClickListener {
            val intent = Intent(this, AddClassNoticeActivity::class.java)
            intent.putExtra(Constants.USER_CLASS, mUserDetails.grade)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivityForResult(intent, SET_NEW_NOTICE)
        }
    }

    fun deleteNotice(position: Int){
        mClassRoom.notice.removeAt(position)
        showProgressDialog("Please wait....")

        FireStoreClass().addUpdateNoticeList(this, mClassRoom, mUserDetails.grade)
    }

    fun onDeleteNotice(){
        hideProgressDialog()
        showProgressDialog("Please wait....")
        FireStoreClass().loadClassRoomData(this, mUserDetails.grade)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SET_NEW_NOTICE){
            showProgressDialog("Please wait....")
            FireStoreClass().loadClassRoomData(this, mUserDetails.grade)
        }
    }

    fun setClassNotice(classRoom: ClassRoom) {
        hideProgressDialog()
        mClassRoom = classRoom
        val listNotice = classRoom.notice

        binding?.rvClassNotice?.layoutManager = LinearLayoutManager(this)
        binding?.rvClassNotice?.setHasFixedSize(true)
        val adapter = ClassNoticeAdapter(this@ClassNoticeActivity, listNotice, mUserDetails)

        binding?.rvClassNotice?.adapter = adapter
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarClassNotice)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Class Notice"
            binding?.toolbarClassNotice?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarClassNotice?.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.deep_blue)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}
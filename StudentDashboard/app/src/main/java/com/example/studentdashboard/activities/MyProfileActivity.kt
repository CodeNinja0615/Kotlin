package com.example.studentdashboard.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityMyProfileBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private var binding: ActivityMyProfileBinding? = null
    private lateinit var mUserDetails: User

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        FireStoreClass().loadUserData(this)

        binding?.cardAttendance?.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }

        binding?.cardContent?.setOnClickListener {
            val intent = Intent(this, ContentActivity::class.java)
            startActivity(intent)
        }

        binding?.cardTimeTable?.setOnClickListener {
            val intent = Intent(this, TimeTableActivity::class.java)
            intent.putExtra(Constants.USER_CLASS, mUserDetails.grade)
            startActivity(intent)
        }

    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfile)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "My Profile"
            binding?.toolbarMyProfile?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarMyProfile?.setNavigationOnClickListener {
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user

        val profileImg = binding?.civProfileImage
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.user_place_holder_black)
            .into(profileImg!!)


        binding?.tvName?.text = user.name
        binding?.tvEmail?.text = user.email
        if (user.mobile != 0L) {
            binding?.tvMobile?.text = user.mobile.toString()
        }
        binding?.tvClass?.text = user.grade
        binding?.tvStudentID?.text = user.studentId.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
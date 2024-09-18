package com.example.studentdashboard.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.MarksAdapter
import com.example.studentdashboard.adapters.StudentAdapter
import com.example.studentdashboard.databinding.ActivityClassStudentsBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import java.util.ArrayList

class ClassStudentsActivity : BaseActivity() {
    private var binding: ActivityClassStudentsBinding? = null
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassStudentsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        if (intent.hasExtra(Constants.USERS)){
            mUserDetails = intent.getParcelableExtra(Constants.USERS)!!
            setupActionBar()
        }

        showProgressDialog("Please wait....")
        FireStoreClass().getStudentsByClass(this, mUserDetails.grade)
    }

    fun setStudentData(usersList: ArrayList<User>) {
        hideProgressDialog()
        binding?.rvClassStudents?.layoutManager = LinearLayoutManager(this)
        binding?.rvClassStudents?.setHasFixedSize(true)
        val adapter = StudentAdapter(this, usersList)

        binding?.rvClassStudents?.adapter = adapter
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarClassStudents)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Students(Class: ${mUserDetails.grade})"
            binding?.toolbarClassStudents?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarClassStudents?.setNavigationOnClickListener {
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
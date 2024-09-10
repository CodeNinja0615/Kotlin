package com.example.studentdashboard.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityTimeTableBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.utils.Constants

class TimeTableActivity : BaseActivity() {
    private var binding: ActivityTimeTableBinding? = null
    private lateinit var mClass: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideSystemUI()
        setupActionBar()
        if (intent.hasExtra(Constants.USER_CLASS)){
            mClass = intent.getStringExtra(Constants.USER_CLASS)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadClassRoomData(this, mClass)

    }

    fun setTimetable(classRoom: ClassRoom) {
        hideProgressDialog()
        if (classRoom.classTimeTable.isNotEmpty()){
            val classTimeTable = binding?.ivClassTimeTable
            binding?.cvClassTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.classTimeTable)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
        }
        if (classRoom.midTerm.isNotEmpty()){
            val classTimeTable = binding?.ivMidTerm
            binding?.cvMidTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.midTerm)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
        }
        if (classRoom.finalTerm.isNotEmpty()){
            val classTimeTable = binding?.ivFinalTerm
            binding?.cvFinalTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.finalTerm)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTimeTable)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Time Table"
            binding?.toolbarTimeTable?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarTimeTable?.setNavigationOnClickListener {
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
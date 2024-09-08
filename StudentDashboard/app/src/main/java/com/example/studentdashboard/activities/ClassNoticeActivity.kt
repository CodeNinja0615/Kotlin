package com.example.studentdashboard.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.ClassNoticeAdapter
import com.example.studentdashboard.databinding.ActivityClassNoticeBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.utils.Constants

class ClassNoticeActivity : BaseActivity() {
    private var binding: ActivityClassNoticeBinding? = null
    private lateinit var mClass: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassNoticeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        if (intent.hasExtra(Constants.USER_CLASS)){
            mClass = intent.getStringExtra(Constants.USER_CLASS)!!
        }

        FireStoreClass().loadClassRoomData(this, mClass)
    }


    fun setClassNotice(classRoom: ClassRoom) {
        val listNotice = classRoom.notice

        binding?.rvClassNotice?.layoutManager = LinearLayoutManager(this)
        binding?.rvClassNotice?.setHasFixedSize(true)
        val adapter = ClassNoticeAdapter(this, listNotice)

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.holo_red_dark)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}
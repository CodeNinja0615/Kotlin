package com.example.studentdashboard.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.AbsenceAdapter
import com.example.studentdashboard.databinding.ActivityAttendanceBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User

class AttendanceActivity : BaseActivity() {
    private var binding: ActivityAttendanceBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        FireStoreClass().loadUserData(this)
    }


    fun setAbsence(loggedInUser: User) {
        val absenceDateList: ArrayList<String> = loggedInUser.absenceDate

        binding?.rvAbsenceDates?.layoutManager = LinearLayoutManager(this)
        binding?.rvAbsenceDates?.setHasFixedSize(true)
        val adapter = AbsenceAdapter(this, absenceDateList)

        binding?.rvAbsenceDates?.adapter = adapter
    }




    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarAttendanceActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Absence Dates"
            binding?.toolbarAttendanceActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white ))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarAttendanceActivity?.setNavigationOnClickListener {
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
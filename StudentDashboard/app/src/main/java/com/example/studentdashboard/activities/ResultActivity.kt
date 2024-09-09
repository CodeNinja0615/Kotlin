package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.ClassNoticeAdapter
import com.example.studentdashboard.adapters.MarksAdapter
import com.example.studentdashboard.databinding.ActivityResultBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User

class ResultActivity : BaseActivity() {
    private var binding: ActivityResultBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        FireStoreClass().loadUserData(this)
    }



    fun setResultData(loggedInUser: User) {

        // Iterate through each Marks object in the User's marks list
        for (mark in loggedInUser.marks) {
            // List of all marks to calculate total
            val marksList = listOf(
                mark.english,
                mark.maths,
                mark.science,
                mark.socialScience,
                mark.hindi,
                mark.computerScience
            )

            // Calculate total marks obtained
            val totalMarks = marksList.sum()

            // Calculate percentage assuming each subject is out of 100
            val numberOfSubjects = marksList.size
            val percentage = (totalMarks / (numberOfSubjects * 100)) * 100

            // Round percentage to two decimal places
            val roundedPercentage = String.format("%.2f", percentage).toDouble()

            // Calculate CGPA
            val cgpa = percentage / 9.5  // Using 9.5 as a standard divisor for percentage to CGPA

            // Round CGPA to two decimal places
            val roundedCgpa = String.format("%.2f", cgpa).toDouble()

            // Determine Grade
            val grade = when {
                percentage >= 90 -> "A+"
                percentage >= 80 -> "A"
                percentage >= 70 -> "B+"
                percentage >= 60 -> "B"
                percentage >= 50 -> "C+"
                percentage >= 40 -> "C"
                else -> "F"
            }

            // Determine Status
            val status = if (percentage >= 40) "Pass" else "Fail"

            // Update the Marks object with calculated values
            mark.apply {
                this.percentage = roundedPercentage
                this.cgpa = roundedCgpa
                this.grade = grade
                this.status = status
            }

            binding?.rvResultData?.layoutManager = LinearLayoutManager(this)
            binding?.rvResultData?.setHasFixedSize(true)
            val adapter = MarksAdapter(this, loggedInUser.marks)

            binding?.rvResultData?.adapter = adapter
        }


    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarResultActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Result"
            binding?.toolbarResultActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarResultActivity?.setNavigationOnClickListener {
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
package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityHelpBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.Feedback
import com.example.studentdashboard.models.User

class HelpActivity : BaseActivity() {
    private var binding: ActivityHelpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        binding?.btnSubmitFeedback?.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            val feedback:String = binding?.etFeedback?.text.toString()
            val newFeedback = Feedback(feedback)
            FireStoreClass().feedBack(this, newFeedback)
        }
    }

    fun feedBackRegisteredSuccess(){
        hideProgressDialog()
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarHelp)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Help"
            binding?.toolbarHelp?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarHelp?.setNavigationOnClickListener {
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
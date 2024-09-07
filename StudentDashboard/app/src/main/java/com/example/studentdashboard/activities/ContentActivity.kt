package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityContentBinding

class ContentActivity : AppCompatActivity() {
    private var binding: ActivityContentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarContentActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Content"
            binding?.toolbarContentActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarContentActivity?.setNavigationOnClickListener {
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
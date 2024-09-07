package com.example.studentdashboard.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivitySplashBinding
import com.example.studentdashboard.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()

        val typeFace : Typeface = Typeface.createFromAsset(assets, "Moderniz.otf")
        binding?.tvAppName?.typeface = typeFace

        Handler().postDelayed({
            val currentUserID = FireStoreClass().getCurrentEmailId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500)

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
        if (binding != null){
            binding = null
        }
    }
}
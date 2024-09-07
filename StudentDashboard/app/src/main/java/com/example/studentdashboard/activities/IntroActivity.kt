package com.example.studentdashboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        binding?.btnSignUp?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding?.btnSignIn?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding?.btnRefreshCode?.setOnClickListener {
            val etCode = binding?.etCode?.text.toString()
            if (etCode == "Admin"){
                binding?.btnSignUp?.visibility = View.VISIBLE
            }else{
                binding?.btnSignUp?.visibility = View.GONE
            }
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
        if (binding != null){
            binding = null
        }
    }
}
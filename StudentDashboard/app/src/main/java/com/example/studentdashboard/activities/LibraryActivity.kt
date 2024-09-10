package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.ClassNoticeAdapter
import com.example.studentdashboard.adapters.LibraryAdapter
import com.example.studentdashboard.databinding.ActivityLibraryBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.models.Library
import com.example.studentdashboard.models.School
import com.example.studentdashboard.utils.Constants

class LibraryActivity : BaseActivity() {
    private var binding: ActivityLibraryBinding? = null
    private lateinit var mClass: String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()
        if (intent.hasExtra(Constants.USER_CLASS)){
            mClass = intent.getStringExtra(Constants.USER_CLASS)!!
        }

        binding?.ivTooltip?.setOnClickListener {
            showTooltip(it, "These books are recommended by the school but might get changed by your teacher. Consult with your teacher before purchase.")
        }

        FireStoreClass().loadLibraryData(this, mClass)

    }


    private fun showTooltip(anchorView: View, tooltipText: String) {
        val popupWindow = PopupWindow(this)
        val textView = TextView(this)

        textView.text = tooltipText
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        textView.setPadding(16, 16, 16, 16)
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))

        popupWindow.contentView = textView
        popupWindow.isFocusable = true

        // Set the popup window size
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT)

        // Show the popup window anchored to the tooltip icon
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    fun setLibraryData(school: School) {
        val listLibrary = school.books

        binding?.rvLibrary?.layoutManager = LinearLayoutManager(this)
        binding?.rvLibrary?.setHasFixedSize(true)
        val adapter = LibraryAdapter(this, listLibrary)

        binding?.rvLibrary?.adapter = adapter
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarLibraryActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Library"
            binding?.toolbarLibraryActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarLibraryActivity?.setNavigationOnClickListener {
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
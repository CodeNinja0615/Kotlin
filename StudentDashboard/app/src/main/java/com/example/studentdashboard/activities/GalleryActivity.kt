package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.ClassNoticeAdapter
import com.example.studentdashboard.adapters.GalleryAdapter
import com.example.studentdashboard.databinding.ActivityGalleryBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.School
import com.example.studentdashboard.models.User

class GalleryActivity : BaseActivity() {
    private var binding: ActivityGalleryBinding? = null
    private lateinit var eventGalleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()
        FireStoreClass().loadGalleryData(this)
    }


    fun setGalleryData(school: School){
        val imageUrls: ArrayList<String> = school.gallery
        val rvEventImages = binding?.rvEventImages

        // Use StaggeredGridLayoutManager for two columns with varying heights
        rvEventImages?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Set up the adapter with the image URLs
        val eventGalleryAdapter: GalleryAdapter = GalleryAdapter(this, imageUrls)
        rvEventImages?.adapter = eventGalleryAdapter
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarGalleryActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Gallery"
            binding?.toolbarGalleryActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarGalleryActivity?.setNavigationOnClickListener {
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
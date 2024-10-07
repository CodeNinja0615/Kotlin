package com.example.studentdashboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.GalleryAdapter
import com.example.studentdashboard.databinding.ActivityGalleryBinding
import com.example.studentdashboard.models.User

class GalleryActivity : BaseActivity() {
    private var binding: ActivityGalleryBinding? = null
    private lateinit var rvEventImages: RecyclerView
    private lateinit var eventGalleryAdapter: GalleryAdapter
    private val imageUrls = arrayListOf<String>(
        "https://firebasestorage.googleapis.com/v0/b/student-dashboard-c0886.appspot.com/o/Results%2Fresult1727200300230.jpg?alt=media&token=a16045ed-8630-4da3-89fa-060d0e581199",
        "https://firebasestorage.googleapis.com/v0/b/student-dashboard-c0886.appspot.com/o/Results%2Fresult1727201825230.jpg?alt=media&token=fd326592-2b92-43ad-8ae0-bb7faf77dac6",
        "https://firebasestorage.googleapis.com/v0/b/student-dashboard-c0886.appspot.com/o/Results%2Fresult1727201958220.jpg?alt=media&token=b5728ae5-9c47-4645-9831-795960efe016",
        "https://firebasestorage.googleapis.com/v0/b/student-dashboard-c0886.appspot.com/o/Results%2Fresult1727202314196.jpg?alt=media&token=03619a40-609b-40a7-a1fb-05d75bcb677f",
        "https://firebasestorage.googleapis.com/v0/b/student-dashboard-c0886.appspot.com/o/Results%2Fresult1727380962327.jpg?alt=media&token=a827f15e-65b6-4812-a984-2ec2784cf5ef"
        // Add more URLs
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        rvEventImages = findViewById(R.id.rvEventImages)

        // Use StaggeredGridLayoutManager for two columns with varying heights
        rvEventImages.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Set up the adapter with the image URLs
        eventGalleryAdapter = GalleryAdapter(this, imageUrls)
        rvEventImages.adapter = eventGalleryAdapter
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
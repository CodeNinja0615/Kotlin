package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private var binding: ActivityHappyPlaceDetailBinding? = null
    private var happyPlaceDetailModel: HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)




        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){ //--- To check if intent has the data that we passed from previous activity
            happyPlaceDetailModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)!! //----- can use type safety (as HappyPlaceModel) with intent.getSerializableExtra
        }

        if (happyPlaceDetailModel != null){
            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceDetailModel!!.title
            binding?.toolbarHappyPlaceDetail?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel!!.image))
            binding?.tvDescription?.text = happyPlaceDetailModel!!.description
            binding?.tvLocation?.text = happyPlaceDetailModel!!.location
        }
    }
}
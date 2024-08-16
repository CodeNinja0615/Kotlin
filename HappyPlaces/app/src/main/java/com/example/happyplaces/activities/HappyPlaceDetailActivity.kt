package com.example.happyplaces.activities

import android.content.Intent
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



        //-------------------------------------Getting Intent Data---------------------------//

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){ //--- To check if intent has the data that we passed from previous activity
            happyPlaceDetailModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)!! //----- can use type safety (as HappyPlaceModel) with intent.getSerializableExtra
        }

        //-------------------------------------Using intent Data---------------------------//
        if (happyPlaceDetailModel != null){ //------Only if object is not null
            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceDetailModel!!.title
            binding?.toolbarHappyPlaceDetail?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            //----Getting the data from previous activity using intent
            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel!!.image))
            binding?.tvDescription?.text = happyPlaceDetailModel!!.description
            binding?.tvLocation?.text = happyPlaceDetailModel!!.location

            //------------------------Moving to Map Activity that show Map Fragment using Google Maps SDK-------------------------//
            binding?.btnViewOnMap?.setOnClickListener {//-------------------- Using Maps SDK API in same key as Place API
                val intent = Intent(this@HappyPlaceDetailActivity, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailModel) //---------Taking data to MapActivity page to use data on map
                startActivity(intent)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (binding!=null){
            binding = null
        }
    }
}
package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMapBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {//------------OnMapReadyCallback using this class call back for SupportMapFragment, onMapReady
    private var mHappyPlaceDetail: HappyPlaceModel? = null
    private var binding: ActivityMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        //-------------------------------------Getting Intent Data for Google Map SDK---------------------------//
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){ //--- To check if intent has the data that we passed from previous activity
            mHappyPlaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)!! //----- can use type safety (as HappyPlaceModel) with intent.getSerializableExtra
        }

        //-------------------------------------Using intent Data---------------------------//
        if (mHappyPlaceDetail != null){//------Only if object is not null
            setSupportActionBar(binding?.toolbarViewOnMap)

            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = mHappyPlaceDetail?.title
                binding?.toolbarViewOnMap?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            }
            binding?.toolbarViewOnMap?.setNavigationOnClickListener {
                onBackPressed()
            }

            //-------------------------creating Map fragment for OnMapReadyCallback to put marker on location
            val supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this@MapActivity)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) { //-----------As soon as the map is ready
        if (mHappyPlaceDetail != null) { //------Checking if obj is not null just to be safe
            val position = LatLng(mHappyPlaceDetail!!.latitude, mHappyPlaceDetail!!.longitude)
            googleMap.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetail!!.location))

            //-----------Zoomed location------------------//
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
            googleMap.animateCamera(newLatLngZoom)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (binding!=null){
            binding = null
        }
    }
}
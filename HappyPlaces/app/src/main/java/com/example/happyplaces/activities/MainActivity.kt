package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlaceAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarMain)
        if(supportActionBar != null){
            supportActionBar?.title = "Happy Place"
            binding?.toolbarMain?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }
        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding?.recyclerViewHappyPlaces?.layoutManager = LinearLayoutManager(this@MainActivity)
        val placesAdapter = HappyPlaceAdapter(happyPlaceList) //---No need to use the context cause using parent context in the adapter
        binding?.recyclerViewHappyPlaces?.setHasFixedSize(true)
        binding?.recyclerViewHappyPlaces?.adapter = placesAdapter
    }


    private fun getHappyPlacesListFromLocalDB(): ArrayList<HappyPlaceModel> {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()
        try {
            if(getHappyPlacesList.size>0){
                binding?.tvNoRecords?.visibility = View.INVISIBLE
                binding?.recyclerViewHappyPlaces?.visibility = View.VISIBLE
                setupHappyPlacesRecyclerView(getHappyPlacesList) //------Putting value in Recycler View
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList() // Return an empty list in case of an error
        }

        return getHappyPlacesList
    }

}
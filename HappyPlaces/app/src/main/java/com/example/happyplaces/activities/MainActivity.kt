package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlaceAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.example.happyplaces.utils.SwipeToDeleteCallback
import com.example.happyplaces.utils.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1 //----------------private const val won't be accessible to other classes
        var EXTRA_PLACE_DETAILS = "extra_place_details" //----------------To migrate data with a given name from one activity to another
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

        //---------------------------------Add Data-------------------------------//
        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB() //------------------To get all data from DB right after entering
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB() //------------------To get all data from DB
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

        //--- To click on a particular list item //-------For detail page
        placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{ //-------Using the setOnClickListener function to execute the functions interface
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this){ //-----------------------------Object For Swipe to Edit
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.recyclerViewHappyPlaces?.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE, this@MainActivity)
                //-------Above line is "ADD_PLACE_ACTIVITY_REQUEST_CODE" to get to "onActivityResult" pretending as adding data just to update DB
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)  //-----------------------------For Swipe to Edit
        editItemTouchHelper.attachToRecyclerView(binding?.recyclerViewHappyPlaces)



        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){ //-----------------------------Object For Swipe to Delete
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.recyclerViewHappyPlaces?.adapter as HappyPlaceAdapter
                adapter.removeAt(viewHolder.adapterPosition,this@MainActivity)
                getHappyPlacesListFromLocalDB() //------------------To get all data from DB in this case check it again
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)  //-----------------------------For Swipe to Delete
        deleteItemTouchHelper.attachToRecyclerView(binding?.recyclerViewHappyPlaces)
    }


    private fun getHappyPlacesListFromLocalDB(): ArrayList<HappyPlaceModel> {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList() //------ storing an Array list of HappyPlaceModel objects
        try {
            if(getHappyPlacesList.size>0){
                binding?.tvNoRecords?.visibility = View.INVISIBLE
                binding?.recyclerViewHappyPlaces?.visibility = View.VISIBLE
                setupHappyPlacesRecyclerView(getHappyPlacesList) //------Putting value in Recycler View
            }else{
                binding?.tvNoRecords?.visibility = View.VISIBLE
                binding?.recyclerViewHappyPlaces?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList() // Return an empty list in case of an error
        }

        return getHappyPlacesList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding!=null){
            binding = null
        }
    }
}
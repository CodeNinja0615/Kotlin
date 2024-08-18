package com.example.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherService
import com.example.weatherapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null


    private lateinit var customProgressDialog: Dialog

    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //-----------------------------------------Fused Location----------------------------------//-------used in function requestNewLocationData below
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUI()

        if (!isLocationEnabled()){
            // Create the AlertDialog
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Enable Location Service")
                .setMessage("Your location service is turned off. Please enable it.")
                .setPositiveButton("OK") { dialog, _ ->

                    Toast.makeText(this@MainActivity, "Turn On Location", Toast.LENGTH_LONG).show()
                    // Navigate to the location settings
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                    dialog.dismiss()
                }
                .setNegativeButton("No, Thanks") { dialog, _ ->
                    // Just dismiss the dialog
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }else{
            //Toast.makeText(this@MainActivity, "Location Turned On", Toast.LENGTH_LONG).show()
            locationPermission() //-----------------Location permission with current Location Data
        }

    }


//-----------------------------------------------------------------------------------------------------------------------------------------//


    //-----------------------To ask location permission permission---------------//
    private fun locationPermission(){ //-------------------Dexter Permission for camera----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@MainActivity, "Location permission granted", Toast.LENGTH_LONG).show()
                    requestLocationData()//-----------Setting the location data post permission grant
                }else{
                    Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check();
    }


    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){ //-----------------------Location Request
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mLocationRequest.interval = 1000
//        mLocationRequest.numUpdates = 1
        showProgressDialog()
        val mLocationRequest =  LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        mLocationRequest.numUpdates = 1
        //-----This will request location update from mLocationCallback i.e. an object of LocationCallback
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()) //---------Using FusedLocation Client

    }

    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            //super.onLocationResult(p0)
            val mLastLocation: Location? = locationResult.lastLocation
            val latitude = mLastLocation!!.latitude
            val longitude = mLastLocation.longitude
            Log.i("Current Lat","$latitude")
            Log.i("Current Lng","$longitude")


            getLocationWeatherDetails(latitude, longitude)
        }
    }


    //--------------Common for all permissions----------------------------//
    private fun showRationalDialogForPermissions() { //------- In case permission is denied
        AlertDialog.Builder(this).setMessage("It looks like you have not enabled the permission request for this feature." +
                " It can enabled under application settings").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
                dialog, _ ->
            dialog.dismiss()
        }.show()
    }

//-----------------------------------------------------------------------------------------------------------------------------------------//

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double){
        if (Constants.isNetworkAvailable(this@MainActivity)){
//            Toast.makeText(this, "Internet available", Toast.LENGTH_LONG).show()
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: WeatherService = retrofit.create<WeatherService>(WeatherService::class.java)

            val listCall : Call<WeatherResponse> = service.getWeather(latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID)

            listCall.enqueue(object : Callback<WeatherResponse>{
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful){
                        cancelProgressDialog()
                        val weatherList: WeatherResponse? = response.body()

                        val weatherResponseJsonString = Gson().toJson(weatherList) //-------Gson to Json to store in shared preference
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()

                        setupUI()
                        Log.i("Response Result :", "$weatherList")
                    }else{
                        val rc = response.code()
                        when(rc){
                            400 -> {
                                Log.e("Error 400: ","Bad Connection")
                            }
                            404 -> {
                                Log.e("Error 404: ","Not Found")
                            }else->{
                                Log.e("Error: ","Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Error: ", t.message.toString())
                }
            })
        }else{
            Toast.makeText(this, "Internet not available", Toast.LENGTH_LONG).show()
        }
    }



    @SuppressLint("SetTextI18n")
    private fun setupUI(){
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")
        if (!weatherResponseJsonString.isNullOrBlank()){
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            for (i in weatherList.weather.indices){
                Log.i("Weather Name: ", weatherList.weather.toString())
                binding?.tvMain?.text = weatherList.weather[i].main
                binding?.tvMainDescription?.text = weatherList.weather[i].description

                binding?.tvTemp?.text = weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                binding?.tvHumidity?.text = weatherList.main.humidity.toString()+"%"

                binding?.tvSunriseTime?.text = unixTime(weatherList.sys.sunrise)
                binding?.tvSunsetTime?.text = unixTime(weatherList.sys.sunset)

                binding?.tvMax?.text = weatherList.main.temp_max.toString() + " max"
                binding?.tvMin?.text = weatherList.main.temp_min.toString() + " min"

                binding?.tvSpeed?.text = weatherList.wind.speed.toString()

                binding?.tvName?.text = weatherList.name
                binding?.tvCountry?.text = weatherList.sys.country

                when(weatherList.weather[i].icon){
                    "01d" -> binding?.ivMain?.setImageResource(R.drawable.sunny)
                    "02d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "03d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "04d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "04n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "10d" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                    "11d" -> binding?.ivMain?.setImageResource(R.drawable.storm)
                    "13d" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
                    "01n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "02n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "03n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "10n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "11n" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                    "13n" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
                    else -> binding?.ivMain?.setImageResource(R.drawable.sunny)
                }
            }
        }

    }

    private fun getUnit(value: String): String?{
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value){
            value = "°F"
        }
        return value
    }

    @SuppressLint("SimpleDateFormat")
    private fun unixTime(timex: Long): String?{
        val date = Date(timex *1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.UK)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    //-----------------------------------------------------------------------------------------------------------------//

    private fun showProgressDialog() {
        // Create a Dialog instance
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog.setContentView(R.layout.dialog_custom_progress)
//        customProgressDialog.setCanceledOnTouchOutside(false)
        customProgressDialog.show()
    }
    private fun cancelProgressDialog() {
        // cancel a Dialog instance
        customProgressDialog.cancel()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_refresh ->{
                requestLocationData()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.stormy_blue)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}
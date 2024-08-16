package com.example.happyplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.example.happyplaces.utils.GetAddressFromLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var binding: ActivityAddHappyPlaceBinding? = null

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    private var mHappyPlaceDetails: HappyPlaceModel? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "ADD HAPPY PLACE"
            binding?.toolbarAddPlace?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }

        //-----------------------------------------Fused Location----------------------------------//-------used in function requestNewLocationData below
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        //-------------------If joining this with Swipe to Edit-----------------//
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){ //-----------------------------For Swipe to Edit
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)!! //------ (as HappyPlaceModel) <- This is needed in serializable
        }


        //-------------------Date Picker Dialog-----------------//
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        updateDateInView()


        //-------------------Check If Editing-----------------//
        if (mHappyPlaceDetails != null){  //-----------------------------For Swipe to Edit
            supportActionBar?.title = "EDIT HAPPY PLACE"

            binding?.etTitle?.setText(mHappyPlaceDetails!!.title)
            binding?.etDescription?.setText(mHappyPlaceDetails!!.description)
            binding?.etDate?.setText(mHappyPlaceDetails!!.date)
            binding?.etLocation?.setText(mHappyPlaceDetails!!.location)

            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)

            binding?.btnSave?.text = "UPDATE"
        }

        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        //-------------------------------------Maps Places Initialize---------------------------//

        //-----------The Places API is not using any Location Services
        //------ It is just using Google Places API i.e. about the places data available on google maps
        if (!Places.isInitialized()){ //----To initialize Places API
            Places.initialize(this@AddHappyPlaceActivity, resources.getString(R.string.google_maps_api_key)) //----Putting the API key in Places
        }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
        binding?.etLocation?.setOnClickListener(this)
        binding?.tvSelectCurrentLocation?.setOnClickListener(this)
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------//



    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------//

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){ //-----------------------Location Request
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000
        mLocationRequest.numUpdates = 1

        //-----This will request location update from mLocationCallback i.e. an object of LocationCallback
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()) //---------Using FusedLocation Client

    }

    private val mLocationCallback = object :LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            //super.onLocationResult(p0)
            val mLastLocation: Location? = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            mLongitude = mLastLocation.longitude
            Log.i("Current Lat","$mLatitude")
            Log.i("Current Lng","$mLongitude")

            //------To get address from LatLng to Geocode and in form of Address string
            val addressTask = GetAddressFromLatLng(this@AddHappyPlaceActivity, mLatitude, mLongitude)
            addressTask.setAddressListener(object: GetAddressFromLatLng.AddressListener{ //-------Using the setAddressListener function to execute the functions interface
                override fun onAddressFound(address: String) {
                    binding?.etLocation?.setText(address)
                }

                override fun onError() {
                    Log.e("get address","Something went wrong")
                }
            })
            addressTask.getAddress() //-------------To execute the Async task

        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------//
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0->{
                            choosePhotoFromGallery()
                        }
                        1->{
//                            Toast.makeText(
//                                this@AddHappyPlaceActivity,
//                                "Camera selection coming soon....",
//                                Toast.LENGTH_LONG).show()
                            takePhotoFromCamera()
                        }
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when {
                    binding?.etTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_LONG).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_LONG).show()
                    }
//                    binding?.etDate?.text.isNullOrEmpty() -> {
//                    Toast.makeText(this, "Please enter date", Toast.LENGTH_LONG).show()
//                    }
                    binding?.etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter location", Toast.LENGTH_LONG).show()
                    }
                    saveImageToInternalStorage == null ->{
                        Toast.makeText(this, "Please select an Image", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id, // it uses 0 if Adding entry else the ID of updating entry
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                            )

                        val dbHandler = DatabaseHandler(this)


                        //-------------------Check If Editing or Adding new data-----------------//

                        if (mHappyPlaceDetails == null) { //----To check if Updating or Adding, null means adding
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                            if (addHappyPlace>0){
                                //Toast.makeText(this, "The Happy Place details are inserted successfully", Toast.LENGTH_LONG).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                            if (updateHappyPlace>0) {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }

                        //DbInspector.show() //-------In case you wnt to see Data base entries
                    }
                }
            }
            //-------------------------------------Putting Location from Places API------------------------------//
            R.id.et_location -> {//-------------Using Places API to gather place data
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, //-----Using the same from onCreate with API Key
                        Place.Field.ADDRESS
                    )

                    //------------------------------------- Open Places API intent to get data ---------------------------//
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@AddHappyPlaceActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            //-------------------------------------Putting Location from GeoLocation API-----------------------------//
            R.id.tv_select_current_location -> {
                if (!isLocationEnabled()){
                    // Create the AlertDialog
                    val alertDialog = AlertDialog.Builder(this)
                        .setTitle("Enable Location Service")
                        .setMessage("Your location service is turned off. Please enable it.")
                        .setPositiveButton("OK") { dialog, _ ->

                            // Navigate to the location settings
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)

                            dialog.dismiss()
                        }
                        .setNegativeButton("No Thanks") { dialog, _ ->
                            // Just dismiss the dialog
                            dialog.dismiss()
                        }
                        .create()
                    alertDialog.show()
                }else{
                    locationPermission() //-----------------Location permission with current Location Data
                }
            }
            else -> {}
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------//

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if (data!=null){
                    val contentURI = data.data //------Getting the data from gallery Intent as Uri/Image paths
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)//------Converting the data at Uri from Image as bitmap
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap) //---Save the image
                        Log.e("Saved Image", "Path:: $saveImageToInternalStorage")
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed!!", Toast.LENGTH_LONG).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap //------Getting the data from camera Intent as bitmap
                val rotatedBitmap = rotateBitmap(thumbnail, 90f) //---- Because the image is -90 degree rotated
                saveImageToInternalStorage = saveImageToInternalStorage(rotatedBitmap) //---Save the image
                Log.e("Saved Image", "Path:: $saveImageToInternalStorage")
                binding?.ivPlaceImage?.setImageBitmap(rotatedBitmap)
            }else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place: Place = Autocomplete.getPlaceFromIntent(data!!) //------Getting the data from Place API Intent
                binding?.etLocation?.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------//

    //-----------------------To ask location permission permission---------------//
    private fun locationPermission(){ //-------------------Dexter Permission for camera----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Location permission granted", Toast.LENGTH_LONG).show()
                    requestNewLocationData()//-----------Setting the location data post permission grant
                }else{
                    Toast.makeText(this@AddHappyPlaceActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check();
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//

    //-----------------------To Capture Image using camera with camera permission---------------//
    private fun takePhotoFromCamera(){ //-------------------Dexter Permission for camera----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Select the image from gallery", Toast.LENGTH_LONG).show()
                    val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE) //-------Opens Camera and after capture returns RESULT_OK post permission grant
                    startActivityForResult(cameraIntent, CAMERA)
                }else{
                    Toast.makeText(this@AddHappyPlaceActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check();
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//

    //-----------------------To Choose Image from gallery with Image read permission---------------//
    private fun choosePhotoFromGallery() { //-------------------Dexter Permission for gallery----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Select the image from gallery", Toast.LENGTH_LONG).show()
                    //---------------------------Opens Gallery and after image selection returns RESULT_OK with image path post permission grant-----------------------------//
                    val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }else{
                    Toast.makeText(this@AddHappyPlaceActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check();
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//

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
                    }catch (e:ActivityNotFoundException){
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel"){
                    dialog, _ ->
                    dialog.dismiss()
                }.show()
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//


    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) //-----Path as default path i.e. data for the app
        file = File(file, "${UUID.randomUUID()}.jpg") //-----generating random name for every image

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//


//    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
//        val filename = "${UUID.randomUUID()}.jpg"
//        val fos: OutputStream?
//        var imageUri: Uri? = null
//
//        try {
//            // Use MediaStore to create an entry and obtain a URI for the image
//            val values = ContentValues().apply {
//                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
//                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + IMAGE_DIRECTORY)
//                put(MediaStore.Images.Media.IS_PENDING, true)
//            }
//
//            // Insert the image into MediaStore and get the content URI
//            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//            // Open an output stream using the URI
//            if (imageUri != null) {
//                fos = contentResolver.openOutputStream(imageUri)
//                fos?.use {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//                }
//            }
//
//            // Mark the image as complete
//            values.clear()
//            values.put(MediaStore.Images.Media.IS_PENDING, false)
//            if (imageUri != null) {
//                contentResolver.update(imageUri, values, null, null)
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//            if (imageUri != null) {
//                // Clean up the entry if there was an error
//                contentResolver.delete(imageUri, null, null)
//                imageUri = null
//            }
//        }
//        return imageUri
//    }



    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }
//------------------------------------------------------------------------------------------------------------------------------------------------//

    override fun onDestroy() {
        super.onDestroy()
        if (binding!=null){
            binding = null
        }
    }
}
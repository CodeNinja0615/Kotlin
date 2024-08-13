package com.example.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.infinum.dbinspector.DbInspector
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
    }

    private val cal = Calendar.getInstance()
    private lateinit var datesetListener: DatePickerDialog.OnDateSetListener
    private var binding: ActivityAddHappyPlaceBinding? = null

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

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

        datesetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }


        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    datesetListener,
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
                            0,
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                            )
                        val dbHandler = DatabaseHandler(this)
                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                        if (addHappyPlace>0){
                            //Toast.makeText(this, "The Happy Place details are inserted successfully", Toast.LENGTH_LONG).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        //DbInspector.show() //-------In case you wnt to see Data base entries
                    }
                }
            }
            else -> {}
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if (data!=null){
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap) //---Save the image
                        Log.e("Saved Image", "Path:: $saveImageToInternalStorage")
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed!!", Toast.LENGTH_LONG).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                val rotatedBitmap = rotateBitmap(thumbnail, 90f) //---- Because the image is -90 degree rotated
                saveImageToInternalStorage = saveImageToInternalStorage(rotatedBitmap) //---Save the image
                Log.e("Saved Image", "Path:: $saveImageToInternalStorage")
                binding?.ivPlaceImage?.setImageBitmap(rotatedBitmap)
            }
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun takePhotoFromCamera(){ //-------------------Dexter Permission for camera----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Select the image from gallery", Toast.LENGTH_LONG).show()
                    val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

    private fun choosePhotoFromGallery() { //-------------------Dexter Permission for gallery----------------------//
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Select the image from gallery", Toast.LENGTH_LONG).show()
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

    private fun showRationalDialogForPermissions() {
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


    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

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

    override fun onDestroy() {
        super.onDestroy()
        if (binding!=null){
            binding = null
        }
    }
}
package com.example.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
    }
    private val cal = Calendar.getInstance()
    private lateinit var datesetListener: DatePickerDialog.OnDateSetListener
    private var binding: ActivityAddHappyPlaceBinding? = null
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
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }


        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
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
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed!!", Toast.LENGTH_LONG).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                val rotatedBitmap = rotateBitmap(thumbnail, 90f) //---- Because the image is -90 degree rotated
                binding?.ivPlaceImage?.setImageBitmap(rotatedBitmap)
            }
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun takePhotoFromCamera(){
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

    private fun choosePhotoFromGallery() { //-------------------Dexter Permission----------------------//
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

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }
}
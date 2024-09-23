package com.example.studentdashboard.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityMakeResultBinding
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import java.io.IOException

class MakeResultActivity : BaseActivity() {
    private var binding: ActivityMakeResultBinding? = null
    private lateinit var mUserDetails: User
    private lateinit var mSelectedImageURI: Uri
    private var saveImageToInternalStorage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMakeResultBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        if (intent.hasExtra(Constants.USERS)){
            mUserDetails = intent.getParcelableExtra(Constants.USERS)!!
            setupActionBar()
        }

        binding?.tvAddImage?.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                when(which){
                    0->{
                        Constants.choosePhotoFromGallery(this)
                    }
                    1->{
//                            Toast.makeText(
//                                this@AddHappyPlaceActivity,
//                                "Camera selection coming soon....",
//                                Toast.LENGTH_LONG).show()
                        Constants.takePhotoFromCamera(this)
                    }
                }
            }
            pictureDialog.show()
        }

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resultImg = binding?.ivAddImage
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data!=null){
                    mSelectedImageURI = data.data!! //------Getting the data from gallery Intent as Uri/Image paths
                    Log.e("Selected Image", "Path:: $mSelectedImageURI")
                    Glide
                        .with(this)
                        .load(mSelectedImageURI)
                        .centerCrop()
                        .placeholder(R.drawable.ic_result_placeholder)
                        .into(resultImg!!)
                }
            }else if(requestCode == Constants.CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap //------Getting the data from camera Intent as bitmap
                val rotatedBitmap = rotateBitmap(thumbnail, 90f) //---- Because the image is -90 degree rotated
//                saveImageToInternalStorage = Constants.saveImageToInternalStorage(applicationContext, rotatedBitmap) //---Save the image
                saveImageToInternalStorage = Constants.saveImageToInternalStorage(this, rotatedBitmap) //---Save the image
                Log.e("Saved Image", "Path:: $saveImageToInternalStorage")
                Glide
                    .with(this)
                    .load(saveImageToInternalStorage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_result_placeholder)
                    .into(resultImg!!)
            }
        }
    }


    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun resultAddedSuccessfully() {
        TODO("Not yet implemented")
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMakeResultActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Create Result ${mUserDetails.name}"
            binding?.toolbarMakeResultActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarMakeResultActivity?.setNavigationOnClickListener {
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
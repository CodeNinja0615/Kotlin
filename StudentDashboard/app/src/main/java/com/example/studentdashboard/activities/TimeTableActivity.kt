package com.example.studentdashboard.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityTimeTableBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class TimeTableActivity : BaseActivity() {
    private var binding: ActivityTimeTableBinding? = null
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mTimeTableImageUrl: String = ""
    private lateinit var mClassRoomData: ClassRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideSystemUI()
        setupActionBar()
        if (intent.hasExtra(Constants.USERS)){
            mUserDetails = intent.getParcelableExtra(Constants.USERS)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadClassRoomData(this, mUserDetails.grade)

        if (mUserDetails.tag != "teacher"){
            binding?.fabAddTimetable?.visibility = View.GONE
        }

        binding?.fabAddTimetable?.setOnClickListener {
            if (mClassRoomData.classTimeTable.isEmpty() || mClassRoomData.midTerm.isEmpty() || mClassRoomData.finalTerm.isEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Constants.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }else{
                Toast.makeText(this, "All the timetables are being used get in touch with admin", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun timeTableUpdated(){
        hideProgressDialog()
        Toast.makeText(this, "Timetable added", Toast.LENGTH_SHORT).show()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadClassRoomData(this, mUserDetails.grade)
    }

    private fun updateTimetableData(){
        var timeTableOf: String = ""
        if (mClassRoomData.classTimeTable.isEmpty()){
            timeTableOf = "classTimeTable"
        }else if (mClassRoomData.midTerm.isEmpty()){
            timeTableOf = "midTerm"
        }else if (mClassRoomData.finalTerm.isEmpty()){
            timeTableOf = "finalTerm"
        }
        if (timeTableOf.isNotEmpty()) {
            FireStoreClass().addUpdateTimeTable(this,timeTableOf,mTimeTableImageUrl,mUserDetails.grade)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null){
            mSelectedImageFileUri = data.data
            try {
                var imageOf: ImageView? = null
                if (mClassRoomData.classTimeTable.isEmpty()){
                    imageOf = binding?.ivClassTimeTable
                }else if (mClassRoomData.midTerm.isEmpty()){
                    imageOf = binding?.ivMidTerm
                }else if (mClassRoomData.finalTerm.isEmpty()){
                    imageOf = binding?.ivFinalTerm
                }
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.user_place_holder_black)
                    .into(imageOf!!)
                if (mSelectedImageFileUri != null) {
                    uploadUserImage()
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadUserImage(){
        showProgressDialog("Please wait....")
        if (mSelectedImageFileUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("${mUserDetails.grade} Timetable/TT_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapShot->
                Log.e("Firebase Image Url:", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.e("Downloadable Image Url:", uri.toString())
                    mTimeTableImageUrl = uri.toString()
                    hideProgressDialog()
                    updateTimetableData()
                }
            }.addOnFailureListener {
                    exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFullImageDialog(imageUrl: String) {
        // Create a dialog with full screen
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_result_fullimage)

        // Reference the ImageView in the dialog
        val imageView: ImageView = dialog.findViewById(R.id.iv_full_image)

        // Load the image into the ImageView using Glide
        Glide
            .with(this)
            .load(imageUrl)
            .fitCenter()
            .placeholder(R.drawable.ic_result_placeholder)
            .into(imageView)

        imageView.setOnClickListener {
            dialog.dismiss()
        }
        // Show the dialog
        dialog.show()
    }

    fun setTimetable(classRoom: ClassRoom) {
        mClassRoomData = classRoom
        hideProgressDialog()
        if (classRoom.classTimeTable.isNotEmpty()){
            val classTimeTable = binding?.ivClassTimeTable
            binding?.cvClassTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.classTimeTable)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
            binding?.cvClassTT?.setOnClickListener {
                showFullImageDialog(classRoom.classTimeTable)
            }
        }
        if (classRoom.midTerm.isNotEmpty()){
            val classTimeTable = binding?.ivMidTerm
            binding?.cvMidTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.midTerm)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
            binding?.cvMidTT?.setOnClickListener {
                showFullImageDialog(classRoom.midTerm)
            }
        }
        if (classRoom.finalTerm.isNotEmpty()){
            val classTimeTable = binding?.ivFinalTerm
            binding?.cvFinalTT?.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(classRoom.finalTerm)
                .fitCenter()
                .placeholder(R.drawable.ic_timetabelplaceholder)
                .into(classTimeTable!!)
            binding?.cvFinalTT?.setOnClickListener {
                showFullImageDialog(classRoom.finalTerm)
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Constants.showRationalDialogForPermissions(this)
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTimeTable)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Time Table"
            binding?.toolbarTimeTable?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarTimeTable?.setNavigationOnClickListener {
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
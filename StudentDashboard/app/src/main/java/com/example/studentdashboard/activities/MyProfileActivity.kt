package com.example.studentdashboard.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityMyProfileBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private var binding: ActivityMyProfileBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""
    private lateinit var mUserDetails: User

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        FireStoreClass().loadUserData(this)

        binding?.civProfileImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding?.cardStudents?.setOnClickListener {
            val intent = Intent(this, ClassStudentsActivity::class.java)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivity(intent)
        }
        binding?.cardAttendance?.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }

        binding?.cardContent?.setOnClickListener {
            val intent = Intent(this, ContentActivity::class.java)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivity(intent)
        }

        binding?.cardTimeTable?.setOnClickListener {
            val intent = Intent(this, TimeTableActivity::class.java)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivity(intent)
        }

        binding?.cardClassNotice?.setOnClickListener {
            val intent = Intent(this, ClassNoticeActivity::class.java)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivity(intent)
        }

        binding?.cardResult?.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(Constants.USERS, mUserDetails)
            startActivity(intent)
        }

        binding?.cardLibrary?.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            intent.putExtra(Constants.USER_CLASS, mUserDetails.grade)
            startActivity(intent)
        }

        binding?.cardForYou?.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            intent.putExtra(Constants.USER_CLASS, mUserDetails.grade)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null){
            mSelectedImageFileUri = data.data
            try {
                val profileImg = binding?.civProfileImage
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.user_place_holder_black)
                    .into(profileImg!!)
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
                .child("Profile Pictures/USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapShot->
                Log.e("Firebase Image Url:", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.e("Downloadable Image Url:", uri.toString())
                    mProfileImageUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                    exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
            hideProgressDialog()
        }
    }


    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var anyChangesMade = false
        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUrl
            anyChangesMade = true
        }
        if (anyChangesMade) {
            FireStoreClass().updateUserProfileData(this, userHashMap)
        }else{
            hideProgressDialog()
        }
    }



    fun profileUpdateSuccess(){
        Toast.makeText(
            this,
            "Image updated successfully",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
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
        setSupportActionBar(binding?.toolbarMyProfile)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "My Profile"
            binding?.toolbarMyProfile?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarMyProfile?.setNavigationOnClickListener {
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

    fun setUserDataInUI(user: User){
        mUserDetails = user

        val profileImg = binding?.civProfileImage
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.user_place_holder_black)
            .into(profileImg!!)


        binding?.tvName?.text = user.name
        binding?.tvEmail?.text = user.email
        if (user.mobile != 0L) {
            binding?.tvMobile?.text = user.mobile.toString()
        }
        binding?.tvClass?.text = user.grade
        binding?.tvStudentID?.text = user.studentId.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
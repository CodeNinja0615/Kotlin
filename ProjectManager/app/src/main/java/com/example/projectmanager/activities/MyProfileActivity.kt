package com.example.projectmanager.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityMyProfileBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
    private var binding: ActivityMyProfileBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String? = null

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
               showImageChooser()
           }else{
               ActivityCompat.requestPermissions(
                   this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                   READ_STORAGE_PERMISSION_CODE
               )
           }
        }
        binding?.btnUpdate?.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data != null){
            mSelectedImageFileUri = data.data
            try {
                val profileImg = binding?.civProfileImage
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(profileImg!!)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        }else{
            showRationalDialogForPermissions()
        }
    }

    private fun showImageChooser(){
        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

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
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
    }

    fun setUserDataInUI(user: User){
        val profileImg = binding?.civProfileImage
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(profileImg!!)
        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage(){
        showProgressDialog("Please wait....")
        if (mSelectedImageFileUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapShot->
                Log.e("Firebase Image Url:", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.e("Downloadable Image Url:", uri.toString())
                    mProfileImageUrl = uri.toString()

                    // TODO Update user profile data
                }
            }.addOnFailureListener {
                exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
            hideProgressDialog()
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
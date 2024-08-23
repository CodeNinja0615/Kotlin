package com.example.projectmanager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityCreateBoardBinding
import com.example.projectmanager.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var binding: ActivityCreateBoardBinding? = null
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUsername = intent.getStringExtra(Constants.NAME)!!
        }

        binding?.civBoardImage?.setOnClickListener {
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
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoard)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Create Board"
            binding?.toolbarCreateBoard?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarCreateBoard?.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null){
            mSelectedImageFileUri = data.data
            try {
                val boardImg = binding?.civBoardImage
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(boardImg!!)
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
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Constants.showRationalDialogForPermissions(this)
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


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
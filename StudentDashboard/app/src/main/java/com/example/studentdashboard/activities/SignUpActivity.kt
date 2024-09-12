package com.example.studentdashboard.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivitySignUpBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setupActionBar()
    }


    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "you have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim{ it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }.lowercase()
        val password: String = binding?.etPassword?.text.toString().trim{ it <= ' '}
        val confirmPassword: String = binding?.etConfirmPassword?.text.toString().trim{ it <= ' '}
        val grade:String = binding?.etClass?.text.toString().trim{ it <= ' '}
        val mobile: Long = binding?.etMobile?.text.toString().trim { it <= ' ' }.toLongOrNull() ?: 0L
        val studentId: Long = binding?.etStudentId?.text.toString().trim { it <= ' ' }.toLongOrNull() ?: 0L
        if (validateForm(name, email, password, confirmPassword, studentId.toString(), mobile.toString(), grade) && password == confirmPassword){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(
                        id = firebaseUser.uid, name = name,
                        email = registeredEmail, mobile = mobile,
                        grade = grade, studentId = studentId
                    )
                    FireStoreClass().registerUser(this, user)
                } else {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }else if(password != confirmPassword){
            showErrorSnackBar("Passwords do not match")
        }
    }


    private fun validateForm(name: String, email: String, password: String,
                             confirmPassword: String, studentId: String, mobile: String, Class: String): Boolean{
        return when{
            TextUtils.isEmpty(name)-> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)-> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("Please enter a password")
                false
            }
            TextUtils.isEmpty(confirmPassword)-> {
                showErrorSnackBar("Please confirm password")
                false
            }
            TextUtils.isEmpty(studentId)-> {
                showErrorSnackBar("Please enter a student ID")
                false
            }
            TextUtils.isEmpty(mobile)-> {
                showErrorSnackBar("Please enter mobile number")
                false
            }
            TextUtils.isEmpty(Class)-> {
                showErrorSnackBar("Please enter Class")
                false
            }
            else -> {true}
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.btnSignUp?.setOnClickListener {
            registerUser()
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
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.STATUS_BAR_HIDDEN
                )
        window.statusBarColor = ContextCompat.getColor(this, R.color.deep_blue)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}
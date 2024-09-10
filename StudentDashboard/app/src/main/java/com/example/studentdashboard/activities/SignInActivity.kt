package com.example.studentdashboard.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivitySignInBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        // ...
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
        setupActionBar()
    }


    fun signInSuccess(loggedInUser: User?) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    private fun signInRegisteredUser(){
        val email: String = binding?.etEmail?.text.toString().trim{ it <= ' '}.lowercase()
        val password: String = binding?.etPassword?.text.toString().trim{ it <= ' '}
        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        FireStoreClass().loadUserData(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }


    private fun validateForm(email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(email)-> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("Please enter a password")
                false
            }

            else -> {true}
        }
    }



    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
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
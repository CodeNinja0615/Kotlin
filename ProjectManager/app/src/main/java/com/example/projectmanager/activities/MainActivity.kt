package com.example.projectmanager.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityMainBinding
import com.example.projectmanager.databinding.MainContentBinding
import com.example.projectmanager.databinding.NavHeaderMainBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null
    private var contentBinding: MainContentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)


        setupActionBar()



        // Access the main_content binding through the include ID
        contentBinding = MainContentBinding.bind(binding?.mainContent!!.root)

        // Now you can access views from main_content.xml
        contentBinding?.tvUserName?.text = "Welcome, User!"

//        val mainContentBind = binding?.mainContent?.tvUserName //-------can Also Use this


        if (binding != null) {
            binding?.navView?.setNavigationItemSelectedListener(this)
        }

        FireStoreClass().loadUserData(this)

    }

    private fun setupActionBar() {
        // Access the toolbar using the binding object
        setSupportActionBar(binding?.appBarMain?.toolbarMainActivity)
        supportActionBar?.let {
            binding?.appBarMain?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_hamburger_menu)
            binding?.appBarMain?.toolbarMainActivity?.setNavigationOnClickListener {
                toggleDrawer()
            }
        }
    }

    private fun toggleDrawer() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("MainActivity", "MenuItem clicked: ${item.itemId}")
        when (item.itemId) {
            R.id.nav_my_profile -> {
                // Handle "My Profile" action
//                Toast.makeText(this, "My Profile", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MyProfileActivity::class.java )
                startActivity(intent)
            }

            R.id.nav_sign_out -> {
                // Handle sign-out action
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        // Close the drawer after item is selected
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User){

//        val navUserImg: CircleImageView? = findViewById(R.id.nav_user_img)

        // Access the NavigationView header using View Binding
        val headerBinding = NavHeaderMainBinding.bind(binding?.navView!!.getHeaderView(0))

        // Access the views from nav_header_main.xml
        val navUserImg = headerBinding.navUserImg
        val tvUsername = headerBinding.tvUsername

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(navUserImg);
        tvUsername.text = user.name

    }
}
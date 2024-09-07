package com.example.studentdashboard.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studentdashboard.R
import com.example.studentdashboard.adapters.SchoolNoticeAdapter
import com.example.studentdashboard.databinding.ActivityMainBinding
import com.example.studentdashboard.databinding.MainContentBinding
import com.example.studentdashboard.databinding.NavHeaderMainBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.School
import com.example.studentdashboard.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null
    private var contentBinding: MainContentBinding? = null

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    private lateinit var mUserName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideSystemUI()
        setupActionBar()

        // Access the main_content binding through the include ID
        contentBinding = MainContentBinding.bind(binding?.appBarMain!!.mainContent.root)

        // Now you can access views from main_content.xml
//        contentBinding?.tvUserName?.text = "Welcome, User!"
//        val mainContentBind = binding?.mainContent?.tvUserName //-------can Also Use this


        if (binding != null) {
            binding?.navView?.setNavigationItemSelectedListener(this)
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this, true)
        FireStoreClass().loadSchoolData(this)


    }

    fun getNotices(school: School){
        contentBinding?.rvNotice?.layoutManager = LinearLayoutManager(this)
        contentBinding?.rvNotice?.setHasFixedSize(true)
        val adapter = SchoolNoticeAdapter(this, school.notice)
        contentBinding?.rvNotice?.adapter = adapter
    }


    private fun setupActionBar() {
        // Access the toolbar using the binding object
        setSupportActionBar(binding?.appBarMain?.toolbarMainActivity)
        supportActionBar?.let {
            supportActionBar?.title = "Home"
            binding?.appBarMain?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_hamburger_menu)
            binding?.appBarMain?.toolbarMainActivity?.setNavigationOnClickListener {
                toggleDrawer()
            }
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.holo_red_dark)
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
            R.id.nav_MyDashboard -> {
                // Handle "My Dashboard" action
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
        hideProgressDialog()

        mUserName = user.name

        // Access the NavigationView header using View Binding
        val headerBinding = NavHeaderMainBinding.bind(binding?.navView!!.getHeaderView(0))

        // Access the views from nav_header_main.xml
        val navUserImg = headerBinding.navUserImg
        val tvUsername = headerBinding.tvUsername

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.user_place_holder)
            .into(navUserImg);
        tvUsername.text = user.name
    }
}
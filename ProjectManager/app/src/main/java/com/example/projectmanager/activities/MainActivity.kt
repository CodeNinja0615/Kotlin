package com.example.projectmanager.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.adapters.BoardItemsAdapter
import com.example.projectmanager.databinding.ActivityMainBinding
import com.example.projectmanager.databinding.MainContentBinding
import com.example.projectmanager.databinding.NavHeaderMainBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import java.util.Objects

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null
    private var contentBinding: MainContentBinding? = null

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName: String
    private lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideSystemUI()
        setupActionBar()

        mSharedPreferences = this
            .getSharedPreferences(Constants.PROJECT_MANAGER_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (tokenUpdated){
            showProgressDialog("Please wait....")
            FireStoreClass().loadUserData(this, true)
        }else{
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(this@MainActivity) {
                instanceResult ->
                updateFCMToken(instanceResult.token)
            }
        }
        // Access the main_content binding through the include ID
        contentBinding = MainContentBinding.bind(binding?.appBarMain!!.mainContent.root)

        // Now you can access views from main_content.xml
//        contentBinding?.tvUserName?.text = "Welcome, User!"
//        val mainContentBind = binding?.mainContent?.tvUserName //-------can Also Use this


        if (binding != null) {
            binding?.navView?.setNavigationItemSelectedListener(this)
        }

        FireStoreClass().loadUserData(this, true)

        binding?.appBarMain?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog("Please wait....")
        FireStoreClass().loadUserData(this, true)
    }


    private fun updateFCMToken(token: String){
        val userHashMap = HashMap<String, Any>()

        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog("Please wait....")
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>){//------Just Displaying not downloading
        hideProgressDialog()
        if (boardsList.size > 0){
            contentBinding?.rvBoardsList?.visibility = View.VISIBLE
            contentBinding?.tvNoBoards?.visibility = View.GONE

            contentBinding?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            contentBinding?.rvBoardsList?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            contentBinding?.rvBoardsList?.adapter = adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

        }else{
            contentBinding?.rvBoardsList?.visibility = View.GONE
            contentBinding?.tvNoBoards?.visibility = View.VISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FireStoreClass().loadUserData(this)
        }else if (resultCode ==Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardsList(this)
        } else{
            Log.e("Cancelled", "Cancelled")
        }
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
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                // Handle sign-out action
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
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

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean){
        hideProgressDialog()
//        val navUserImg: CircleImageView? = findViewById(R.id.nav_user_img)

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
        if (readBoardsList){
            showProgressDialog("Please wait....")
            FireStoreClass().getBoardsList(this)
        }
    }
}
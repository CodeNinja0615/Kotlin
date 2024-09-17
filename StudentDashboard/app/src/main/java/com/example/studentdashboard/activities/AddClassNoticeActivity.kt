package com.example.studentdashboard.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.studentdashboard.R
import com.example.studentdashboard.databinding.ActivityAddClassNoticeBinding
import com.example.studentdashboard.firebase.FireStoreClass
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.models.Notice
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants

class AddClassNoticeActivity : BaseActivity() {
    private var binding: ActivityAddClassNoticeBinding? = null
    private lateinit var mClass: String
    private lateinit var mClassRoom: ClassRoom
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassNoticeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()
        setupActionBar()

        if (intent.hasExtra(Constants.USER_CLASS) && intent.hasExtra(Constants.USERS)){
            mClass = intent.getStringExtra(Constants.USER_CLASS)!!
            mUserDetails = intent.getParcelableExtra(Constants.USERS)!!
        }
        FireStoreClass().loadClassRoomData(this, mClass)

        binding?.btnSubmitNotice?.setOnClickListener {
            val title: String = binding?.etNoticeTitle?.text.toString()
            val description: String = binding?.etNoticeDescription?.text.toString()
            addClassNotice(title, description)

        }

    }

    fun setClassRoomData(mClassRoom: ClassRoom){
        this.mClassRoom = mClassRoom
    }

    private fun addClassNotice(title: String, description: String){

        val notice = Notice(
            title,
            mUserDetails.name,
            description
        )

        mClassRoom.notice.add(0, notice)
        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateNoticeList(this, mClassRoom, mClass)
    }


    fun noticeAddedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarAddNotice)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Add Notice"
            binding?.toolbarAddNotice?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarAddNotice?.setNavigationOnClickListener {
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
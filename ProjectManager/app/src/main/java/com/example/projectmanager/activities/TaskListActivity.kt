package com.example.projectmanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityTaskListBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.utils.Constants

class TaskListActivity : BaseActivity() {
    private var binding: ActivityTaskListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog("Please wait....")
        FireStoreClass().getBoardsDetails(this, boardDocumentId)

    }


    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)
    }



    private fun setupActionBar(title: String){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = title
            binding?.toolbarTaskListActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24) //------ No need for this
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener { onBackPressed() }
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
package com.example.projectmanager.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanager.R
import com.example.projectmanager.adapters.TaskListItemsAdapter
import com.example.projectmanager.databinding.ActivityTaskListBinding
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.Card
import com.example.projectmanager.models.Task
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants

class TaskListActivity : BaseActivity() {
    private var binding: ActivityTaskListBinding? = null

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>
    companion object{
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAIL_REQUEST_CODE: Int = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        hideSystemUI()


        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog("Please wait....")
        FireStoreClass().getBoardsDetails(this, mBoardDocumentId)//----------To load the page

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAIL_REQUEST_CODE){
            showProgressDialog("Please wait....")
            FireStoreClass().getBoardsDetails(this, mBoardDocumentId)//----------To reload the page
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){ //----------To get the card detail associated to task list item and pass to the next activity
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)  //-----Parcelable class object
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)  //------Int for Index Positions
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)  //------Int for Index Positions
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)  //-----Parcelable class object
        startActivityForResult(intent, CARD_DETAIL_REQUEST_CODE)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog("Please wait....")
        FireStoreClass().getBoardsDetails(this, mBoardDetails.documentId)//----------To reload the page upon adding new task or card
    }


    fun createTaskList(/*position: Int,*/ taskListName: String){
        val task = Task(
            taskListName,
            FireStoreClass().getCurrentUserId()
        )
        mBoardDetails.taskList.add(/*position, */0,task) //-----Add task at 0 index can also create at position value
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) //----Removing Dummy Task from last index before updating FireStore
        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)//----Removing Dummy Task from last index before updating FireStore
        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)//----Removing Dummy Task from last index before updating FireStore
        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)//----Removing Dummy Task from last index before updating FireStore
//        showProgressDialog("Please wait....")
        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserId()) //----Adding the creator to "assigned to" in card(Not board)
//        for (i in Board().assignedTo){//---- To assign all the current board users to the card "assigned to" (no need doing it later)
//            cardAssignedUsersList.add(i)
//        }
        val card = Card(
            cardName,
            FireStoreClass().getCurrentUserId(),
            cardAssignedUsersList
        )

        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )

        mBoardDetails.taskList[position] = task
        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun boardDetails(board: Board){ //------------To get the tasks from the Board model/collection
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        showProgressDialog("Please wait....")
        FireStoreClass().getAssignedMemberListDetails(this, mBoardDetails.assignedTo)
    }

    fun boardMembersDetailsList(list: ArrayList<User>){
        mAssignedMemberDetailList = list
        hideProgressDialog()

        val addTaskList = Task("Add List")//------Adding Dummy task always at last
        mBoardDetails.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)
        binding?.rvTaskList?.adapter = adapter

    }


    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>){ //--------------Drag Drop Card
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)//----Removing Dummy Task from last index before updating FireStore

        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //------For three dot on the right side of TopBar
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = mBoardDetails.name
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
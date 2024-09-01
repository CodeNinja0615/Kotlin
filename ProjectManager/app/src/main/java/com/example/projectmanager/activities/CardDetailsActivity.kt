package com.example.projectmanager.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectmanager.R
import com.example.projectmanager.adapters.CardMemberListItemsAdapter
import com.example.projectmanager.databinding.ActivityCardDetailsBinding
import com.example.projectmanager.dialogs.LabelColorListDialog
import com.example.projectmanager.dialogs.MembersListDialog
import com.example.projectmanager.firebase.FireStoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.Card
import com.example.projectmanager.models.SelectedMembers
import com.example.projectmanager.models.Task
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null
    private lateinit var mBoardDetails: Board
    private var mSelectedColor = ""
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private lateinit var mMembersDetailsList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        hideSystemUI()
        getIntentData()
        setupActionBar()

        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor //----Setting the color if already selected
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if (binding?.etNameCardDetails?.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this, "No changes found", Toast.LENGTH_LONG).show()
            }
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }
        binding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()

        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate
        if (mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            binding?.tvSelectDueDate?.text = selectedDate
        }
        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }
    }


    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun updateCardDetails(){ //-----------Adding data to FireStore boards collection
        val card = Card( //-----------card details with corresponding task
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo, //-----To assign the new member to the card is selected
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1) //----Removing Dummy Task from last index before updating FireStore
        
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }


    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)//----Removing Dummy Task from last index before updating FireStore

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog("Please wait....")
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#8B00FF") // Violet
        colorsList.add("#4B0082") // Indigo
        colorsList.add("#0000FF") // Blue
        colorsList.add("#00FF00") // Green
        colorsList.add("#FFFF00") // Yellow
        colorsList.add("#FFA500") // Orange
        colorsList.add("#FF0000") // Red
        return colorsList
    }

    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }


    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }


    private fun membersListDialog(){
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if (cardAssignedMembersList.size > 0){
            for (i in mMembersDetailsList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailsList[i].id == j){ //------checking that every member assigned to the list is assigned to the card
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMembersDetailsList.indices){
                mMembersDetailsList[i].selected = false
            }
        }

        val listDialog = object: MembersListDialog(
            this,
            mMembersDetailsList,
            resources.getString(R.string.str_select_label_color)
            ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardPosition].assignedTo.contains(user.id)){

                        mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardPosition].assignedTo.add(user.id) //------Adding the member from Board to Card
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailsList.indices){
                        if (mMembersDetailsList[i].id == user.id){
                            mMembersDetailsList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }

        }
        listDialog.show()
    }

    private fun setupSelectedMembersList(){
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
        for (i in mMembersDetailsList.indices){
            for (j in cardAssignedMembersList){
                if (mMembersDetailsList[i].id == j){ //------checking that every member assigned to the list is assigned to the card
                    val selectedMember = SelectedMembers(
                        mMembersDetailsList[i].id,
                        mMembersDetailsList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if (selectedMembersList.size > 0){
            selectedMembersList.add((SelectedMembers("", ""))) //----For + btn
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE

            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            binding?.rvSelectedMembersList?.adapter = adapter

            adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })

        }else{
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }

    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear+1}" else "${monthOfYear+1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                binding?.tvSelectDueDate?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
                val theDate = sdf.parse(selectedDate)

                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun alertDialogToDeleteCard(cardName: String){
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Set the title and message
        builder.setTitle("Alert")
        builder.setMessage("Do you want to delete the card $cardName?")
        builder.setIcon(R.drawable.ic_alert_icon)

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }

        // Set the negative button (No)
        builder.setNegativeButton("No") { dialogInterface, _ ->
            // You can add more logic here if needed
            dialogInterface.dismiss()
        }


        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //------For three dot on the right side of TopBar
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionDeleteMember->{
                val cardName = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
                alertDialogToDeleteCard(cardName)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCardDetailActivity)
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
            binding?.toolbarCardDetailActivity?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding?.toolbarCardDetailActivity?.setNavigationOnClickListener { onBackPressed() }
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
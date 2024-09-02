package com.example.projectmanager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmanager.activities.CardDetailsActivity
import com.example.projectmanager.activities.CreateBoardActivity
import com.example.projectmanager.activities.MainActivity
import com.example.projectmanager.activities.MembersActivity
import com.example.projectmanager.activities.MyProfileActivity
import com.example.projectmanager.activities.SignInActivity
import com.example.projectmanager.activities.SignUpActivity
import com.example.projectmanager.activities.TaskListActivity
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){ //------------To Create/Store user data in firestore with name and user's details
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())//----- Creating document with current user ID
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error Writing the document: $e")
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){ //------------To Create/Store Board data in firestore with name and user's details
        mFireStore.collection(Constants.BOARDS)
            .document()//----- Creating document with with random ID
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created successfully")
                Toast.makeText(activity, "Board Created successfully", Toast.LENGTH_LONG).show()

                activity.boardCreatedSuccessfully()
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Toast.makeText(activity, "Error while creating board", Toast.LENGTH_SHORT).show()
                Log.e(activity.javaClass.simpleName, "Error while creating board", e)
            }
    }

    fun getBoardsList(activity: MainActivity){ //------------To Fetch list of Boards from firestore which the user is assigned to
        mFireStore.collection(Constants.BOARDS)
//            .whereEqualTo(Constants.ASSIGNED_TO, getCurrentUserId())
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())//---- Fetching the data from board document where the assignedTo(array list) has user's ID
            .get()
            .addOnSuccessListener {document->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList) //----- To update the data in adapter via MainActivity for recycler view
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetching board data", e)

            }
    }


    fun getBoardsDetails(activity: TaskListActivity, documentID: String){//------------To get board details
        mFireStore.collection(Constants.BOARDS)
            .document(documentID)//---The board that user has clicked on
            //-----Getting documentID(not in Firestore but from the document ID itself) from getBoardsList to the Board model then via TaskListActivity
            .get()
            .addOnSuccessListener {document->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id //-----No need I think
                activity.boardDetails(board)
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while fetching board data", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }


    fun addUpdateTaskList(activity: Activity, board: Board){//------To add/update data in taskList
        val taskListHashmap = HashMap<String, Any>()

        taskListHashmap[Constants.TASK_LIST] = board.taskList //------Hash map to update task list(array list) from Board Model

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)//---The board that user has clicked on
            .update(taskListHashmap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList Updated Successfully")
                if (activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                }else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener {e->
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "error while updating task list", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun getAssignedMemberListDetails(activity: Activity, assignedTo: ArrayList<String>){//------To fetch members details LIST assigned to board for RV
        mFireStore.collection(Constants.USERS)//----- "Users" document in firestore
            .whereIn(Constants.ID, assignedTo)//--------- Matching all the assigned to IDs to User collection IDs
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList() //----------List of users

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)//----adding every data to the array list
                }
                if (activity is MembersActivity){
                    activity.setUpMembersList(usersList)
                }else if (activity is TaskListActivity){
                    activity.boardMembersDetailsList(usersList)
                }
            }.addOnFailureListener {e->
                if (activity is MembersActivity){
                    activity.hideProgressDialog()
                }else if (activity is TaskListActivity){
                activity.hideProgressDialog()
            }
                Log.e(activity.javaClass.simpleName, "error while fetching members list", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){//------To get the member data from Firestore with email search to assign board
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document->
                if (document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!! //---- Type casting
                    activity.memberDetails(user)  //-----------This function will will assign the new found data to board model
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member!")
                }
            }.addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error",
                    e
                )
            }
    }


    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){//------Assigning member to board from Board collection
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo //----------adding all the values in board model object to hash map
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)//---The board that user has clicked on
            .update(assignedToHashMap)//------adding new user to assigned to list using hashmap
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error",
                    e
                )
            }

    }


    fun getCurrentUserId(): String{ //-----------To get the signed in user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun updateUserProfileData(activity: Activity,
                              userHashMap: HashMap<String, Any>){ //-------------------To update user's profile information
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data Updated Successfully")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                when(activity) {
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "error while creating a board", e)
                Toast.makeText(activity, "Error updating the profile", Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){ //---------------To load user's data in different activity
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document ->
               val loggedInUser = document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }

                }
            }.addOnFailureListener { e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity ->{
                        activity.hideProgressDialog()
                    }

                }
                Log.e("SignInUser", "Error getting the document: $e")
            }
    }
}
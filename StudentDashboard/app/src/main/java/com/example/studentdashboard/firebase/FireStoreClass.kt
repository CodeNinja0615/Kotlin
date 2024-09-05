package com.example.studentdashboard.firebase

import android.app.Activity
import android.util.Log
import com.example.studentdashboard.activities.MainActivity
import com.example.studentdashboard.activities.MyProfileActivity
import com.example.studentdashboard.activities.SignInActivity
import com.example.studentdashboard.activities.SignUpActivity
import com.example.studentdashboard.models.User
import com.example.studentdashboard.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass() {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) { //------------To Create/Store user data in firestore with name and user's details
        mFireStore.collection(Constants.USERS)
            .document(getCurrentEmailId())//----- Creating document with current student ID
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error Writing the document: $e")
            }
    }
    fun getCurrentEmailId(): String{ //-----------To get the signed in user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.email.toString()
        }
        return currentUserID
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){ //---------------To load user's data in different activity
        mFireStore.collection(Constants.USERS)
            .document(getCurrentEmailId()).get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
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
//                    is MyProfileActivity ->{
//                        activity.hideProgressDialog()
//                    }

                }
                Log.e("SignInUser", "Error getting the document: $e")
            }
    }
}
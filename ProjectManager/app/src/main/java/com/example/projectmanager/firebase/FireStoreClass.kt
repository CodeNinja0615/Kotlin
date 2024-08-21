package com.example.projectmanager.firebase

import android.util.Log
import com.example.projectmanager.activities.SignInActivity
import com.example.projectmanager.activities.SignUpActivity
import com.example.projectmanager.models.User
import com.example.projectmanager.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error Writing the document: $e")
            }
    }

    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document ->
               val loggedInUser = document.toObject(User::class.java)!!
                activity.signInSuccess(loggedInUser)
            }.addOnFailureListener { e ->
                Log.e("SignInUser", "Error getting the document: $e")
            }
    }
}
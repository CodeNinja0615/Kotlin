package com.example.studentdashboard.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.studentdashboard.activities.AddClassNoticeActivity
import com.example.studentdashboard.activities.AddResultActivity
import com.example.studentdashboard.activities.AttendanceActivity
import com.example.studentdashboard.activities.ClassNoticeActivity
import com.example.studentdashboard.activities.ClassStudentsActivity
import com.example.studentdashboard.activities.ContentActivity
import com.example.studentdashboard.activities.GalleryActivity
import com.example.studentdashboard.activities.HelpActivity
import com.example.studentdashboard.activities.LibraryActivity
import com.example.studentdashboard.activities.MainActivity
import com.example.studentdashboard.activities.MakeResultActivity
import com.example.studentdashboard.activities.MyProfileActivity
import com.example.studentdashboard.activities.ResultActivity
import com.example.studentdashboard.activities.SignInActivity
import com.example.studentdashboard.activities.SignUpActivity
import com.example.studentdashboard.activities.TimeTableActivity
import com.example.studentdashboard.models.ClassRoom
import com.example.studentdashboard.models.Feedback
import com.example.studentdashboard.models.Marks
import com.example.studentdashboard.models.School
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
                    is AttendanceActivity ->{
                        activity.setAbsence(loggedInUser)
                    }
                    is ResultActivity -> {
                        activity.setResultData(loggedInUser)
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
                    is AttendanceActivity ->{
                        activity.hideProgressDialog()
                    }
                    is ResultActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser", "Error getting the document: $e")
            }
    }

    fun loadClassRoomData(activity: Activity, grade: String){ //---------------To load user's data in different activity
        mFireStore.collection(Constants.CLASS_CONTENT)
            .document(grade).get()
            .addOnSuccessListener {document ->
                val classRoom = document.toObject(ClassRoom::class.java)!!
                when(activity) {
                    is TimeTableActivity -> {
                        activity.setTimetable(classRoom)
                    }
                    is ClassNoticeActivity ->{
                        activity.setClassNotice(classRoom)
                    }
                    is ContentActivity ->{
                        activity.setContentData(classRoom)
                    }
                    is AddClassNoticeActivity ->{
                        activity.setClassRoomData(classRoom)
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Class Room Data", "Error getting the document: $e")
            }
    }

    fun loadLibraryData(activity: LibraryActivity, grade: String){ //---------------To load book's data in different LibraryActivity
        val numericGrade = grade.split("-")[0]
        mFireStore.collection(Constants.SCHOOL_CONTENT)
            .document(numericGrade).get()
            .addOnSuccessListener {document ->
                val school = document.toObject(School::class.java)!!
                activity.setLibraryData(school)
            }.addOnFailureListener { e ->
                Log.e("Class Room Data", "Error getting the document: $e")
            }
    }

    fun loadSchoolData(activity: MainActivity){ //---------------To load user's data in different activity
        mFireStore.collection(Constants.SCHOOL_CONTENT)
            .document(Constants.SCHOOL).get()
            .addOnSuccessListener {document ->
                val school = document.toObject(School::class.java)!!
                activity.setSchoolData(school)
            }.addOnFailureListener { e ->
                Log.e("Class Room Data", "Error getting the document: $e")
            }
    }


    fun updateUserProfileData(activity: Activity,
                              userHashMap: HashMap<String, Any>){ //-------------------To update user's profile information
        mFireStore.collection(Constants.USERS)
            .document(getCurrentEmailId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data Added Successfully")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                when(activity) {
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(activity) {
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "error while uploading profile", e)
                Toast.makeText(activity, "Error updating the profile", Toast.LENGTH_LONG).show()
            }
    }

    fun addUpdateNoticeList(activity: Activity, classRoom: ClassRoom, grade: String){//------To add/update data in notice
        val noticeListHashmap = HashMap<String, Any>()

        noticeListHashmap[Constants.NOTICE] = classRoom.notice //------Hash map to update notice (array list) from ClassRoom Model

        mFireStore.collection(Constants.CLASS_CONTENT)
            .document(grade)
            .update(noticeListHashmap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList Updated Successfully")
                when(activity) {
                    is ClassNoticeActivity -> {
                        activity.onDeleteNotice()
                    }
                    is AddClassNoticeActivity -> {
                        activity.noticeAddedSuccessfully()
                    }
                }
            }
            .addOnFailureListener {e->
                when(activity) {
                    is AddClassNoticeActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "error while updating task list", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }


    fun getStudentsByClass(activity: Activity, grade: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.GRADE, grade)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                when(activity){
                    is ClassStudentsActivity -> {
                        activity.setStudentData(usersList)
                    }
                    is ResultActivity ->{
                        activity.setStudentData(usersList)
                    }
                }
            }.addOnFailureListener {e->
                when(activity){
                    is ClassStudentsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ResultActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "error while fetching members list", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun getStudentByStudentID(activity: Activity, studentID: Long) {
        mFireStore.collection(Constants.USERS) // Access the "Users" collection in Firestore
            .whereEqualTo(Constants.STUDENT_ID, studentID) // Filter by the desired student ID
            .limit(1) // Fetch only one result
            .get()
            .addOnSuccessListener { document ->
                if (document.documents.isNotEmpty()) {
                    val studentDoc = document.documents[0] // Get the first (and only) document
                    val user = studentDoc.toObject(User::class.java) // Convert the document to User object

                    // Pass the single user object back to the UI or activity
                    when (activity) {
                        is AddResultActivity -> {
                            activity.setStudentDataInUI(user!!)
                        }
                    }
                } else {
                    // Handle the case where no matching student is found
                    Toast.makeText(activity, "No student found with this ID", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                when (activity) {
                    is AddResultActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while fetching student data", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }
    fun addUpdateMarksList(activity: MakeResultActivity, user: User) {
        // Hash map to update the user's marks
        val marksListHashmap = HashMap<String, Any>()

        // First, find the document with the matching student ID
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.STUDENT_ID, user.studentId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Get the first matching document (assuming student IDs are unique)
                    val documentId = documents.documents[0].id

                    // Get the existing marks from Firestore
                    val existingMarks = documents.documents[0].get(Constants.MARKS) as? ArrayList<Marks>

                    // Add new marks to the existing list
                    val updatedMarksList = existingMarks ?: arrayListOf()
                    updatedMarksList.addAll(user.marks) // Append new marks to the list

                    // Update the hash map with the updated marks list
                    marksListHashmap[Constants.MARKS] = updatedMarksList

                    // Now update the document using the document ID
                    mFireStore.collection(Constants.USERS)
                        .document(documentId)
                        .update(marksListHashmap)
                        .addOnSuccessListener {
                            activity.resultAddedSuccessfully()
                            Log.e(activity.javaClass.simpleName, "Marks Updated Successfully")
                        }
                        .addOnFailureListener { e ->
                            activity.hideProgressDialog()
                            Log.e(activity.javaClass.simpleName, "Error while updating marks", e)
                            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                        }
                } else {
                    // No matching student found
                    activity.hideProgressDialog()
                    Toast.makeText(activity, "Student not found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error finding student", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun addUpdateTimeTable(activity: Activity,timeTableOf: String, uri: String, grade: String){//------To add/update data in timetable
        val timeTableHashmap = HashMap<String, Any>()

        timeTableHashmap[timeTableOf] = uri //------Hash map to update timetable from ClassRoom Model

        mFireStore.collection(Constants.CLASS_CONTENT)
            .document(grade)
            .update(timeTableHashmap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Time Table Updated Successfully")
                when(activity) {
                    is TimeTableActivity -> {
                        activity.timeTableUpdated()
                    }
                }
            }
            .addOnFailureListener {e->
                when(activity) {
                    is TimeTableActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "error while updating task list", e)
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun getStudentCount(activity: MainActivity) {
        // Access the Firestore collection

        // Query the USERS collection where tag equals "student"
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.TAG, Constants.STUDENT)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Get the number of users with tag = "student"
                val studentCount = querySnapshot.size()
                activity.loadTotalStudents(studentCount)
                // Do something with the count
                Log.d(activity.javaClass.simpleName, "Number of students: $studentCount")
//                Toast.makeText(activity, "Number of students: $studentCount", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e(activity.javaClass.simpleName, "Error fetching student count", e)
                Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun feedBack(activity: HelpActivity, feedback: Feedback) { //------------To Create/Store user data in firestore with name and user's details
        mFireStore.collection(Constants.FEEDBACK)
            .document(getCurrentEmailId())//----- Creating document with current student ID
            .set(feedback, SetOptions.merge())
            .addOnSuccessListener {
                activity.feedBackRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error Writing the document: $e")
            }
    }

    fun loadGalleryData(activity: GalleryActivity){ //---------------To load school's data in different activity
        mFireStore.collection(Constants.SCHOOL_CONTENT)
            .document(Constants.GALLERY).get()
            .addOnSuccessListener {document ->
                val school = document.toObject(School::class.java)!!
                activity.setGalleryData(school)
            }.addOnFailureListener { e ->
                Log.e("Class Room Data", "Error getting the document: $e")
            }
    }

    fun getStudentInClassCount(activity: MyProfileActivity, grade: String) {
        // Access the Firestore collection and perform a compound query
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.TAG, Constants.STUDENT)
            .whereEqualTo(Constants.GRADE, grade)  // Add the second condition
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Get the number of users matching the criteria
                val studentCount = querySnapshot.size()
                activity.loadTotalStudentsInClass(studentCount)

                // Log the count
                Log.d(activity.javaClass.simpleName, "Number of students in 12-A: $studentCount")
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e(activity.javaClass.simpleName, "Error fetching student count", e)
                Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
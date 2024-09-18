package com.example.studentdashboard.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap

object Constants {

    const val TEACHER_SIGNED_IN: String = "teacher"


    const val USERS: String = "Users" //-- Collection name
    const val CLASS_CONTENT: String = "classcontent"
    const val USER_CLASS: String = "class"
    const val SCHOOL_CONTENT: String = "schoolcontent"
    const val SCHOOL: String = "school"
    const val IMAGE: String = "image"
    const val NOTICE: String = "notice"
    const val GRADE: String = "grade"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    fun showImageChooser(activity: Activity){
        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


    fun showRationalDialogForPermissions(activity: Activity) { //------- In case permission is denied
        AlertDialog.Builder(activity).setMessage("It looks like you have not enabled the permission request for this feature." +
                " It can enabled under application settings").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
                dialog, _ ->
            dialog.dismiss()
        }.show()
    }

}
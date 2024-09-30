package com.example.studentdashboard.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

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
    const val MARKS: String = "marks"
    const val STUDENT_ID: String = "studentId"
    const val ADD_RESULT = 15
    const val CAMERA = 3
    const val IMAGE_DIRECTORY = "ResultImages"
    const val TAG = "tag"
    const val STUDENT = "student"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    fun showImageChooser(activity: Activity){
        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    //-----------------------To Choose Image from gallery with Image read permission---------------//
    fun choosePhotoFromGallery(activity: Activity) { //-------------------Dexter Permission for gallery----------------------//
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //---------------------------Opens Gallery and after image selection returns RESULT_OK with image path post permission grant-----------------------------//
                    showImageChooser(activity)
                }else{
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions(activity)
            }
        }).onSameThread().check();
    }


    fun takePhotoFromCamera(activity: Activity){ //-------------------Dexter Permission for camera----------------------//
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    //Toast.makeText(this@AddHappyPlaceActivity, "Select the image from gallery", Toast.LENGTH_LONG).show()
                    val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE) //-------Opens Camera and after capture returns RESULT_OK post permission grant
                    activity.startActivityForResult(cameraIntent, CAMERA)
                }else{
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:List<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions(activity)
            }
        }).onSameThread().check();
    }

//    fun saveImageToInternalStorage(applicationContext: Context, bitmap: Bitmap):Uri{
//        val wrapper = ContextWrapper(applicationContext)
//        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) //-----Path as default path i.e. data for the app
//        file = File(file, "${UUID.randomUUID()}.jpg") //-----generating random name for every image
//
//        try {
//            val stream: OutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//            stream.flush()
//            stream.close()
//        }catch (e: IOException){
//            e.printStackTrace()
//        }
//        return Uri.parse(file.absolutePath)
//    }




    fun saveImageToInternalStorage(activity: Activity, bitmap: Bitmap): Uri? {
        val filename = "${UUID.randomUUID()}.jpg"
        val fos: OutputStream?
        var imageUri: Uri? = null

        try {
            // Use MediaStore to create an entry and obtain a URI for the image
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + IMAGE_DIRECTORY)
                put(MediaStore.Images.Media.IS_PENDING, true)
            }

            // Insert the image into MediaStore and get the content URI
            imageUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            // Open an output stream using the URI
            if (imageUri != null) {
                fos = activity.contentResolver.openOutputStream(imageUri)
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
            }

            // Mark the image as complete
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            if (imageUri != null) {
                activity.contentResolver.update(imageUri, values, null, null)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            if (imageUri != null) {
                // Clean up the entry if there was an error
                activity.contentResolver.delete(imageUri, null, null)
                imageUri = null
            }
        }
        return imageUri
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
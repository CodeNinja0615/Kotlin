package sameer.example.permissionsdemo

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private  val cameraResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
                isGranted ->
            if (isGranted){
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    private  val cameraAndLocationResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted){
                    if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION){
                        Toast.makeText(this, "Fine Location Permission Granted", Toast.LENGTH_LONG).show()
                    }else if (permissionName == Manifest.permission.ACCESS_COARSE_LOCATION){
                        Toast.makeText(this, "Coarse Location Permission Granted", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show()
                    }
                }else{
                    if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION){
                        Toast.makeText(this, "Fine Location Permission Denied", Toast.LENGTH_LONG).show()
                    }else if (permissionName == Manifest.permission.ACCESS_COARSE_LOCATION){
                        Toast.makeText(this, "Coarse Location Permission Denied", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCameraPermission: Button = findViewById(R.id.btn_camera_permission)
        btnCameraPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M &&
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) &&
                (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))){
                showRationalDialog("Permission Demo requires permissions",
                    "Features cannot be used because access is denied")
            }else{
                cameraAndLocationResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }


    private fun showRationalDialog(
        title:String,
        message:String
    ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel"){dialog, _ ->
                dialog.dismiss()
        }
        builder.create().show()
    }
}
package sameer.example.customdialog

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageBtn : ImageButton = findViewById(R.id.image_btn)
        val alertDialog : Button = findViewById(R.id.alert_btn)
        val customDialog : Button = findViewById(R.id.custom_dialog)
        val customProgressDialogButton: Button = findViewById(R.id.custom_progress_dialog)

        imageBtn.setOnClickListener{view->
            Snackbar.make(view, "Snack Bar Clicked", Snackbar.LENGTH_LONG).show()
        }

        alertDialog.setOnClickListener {
            alertDialogFunction()
        }

        customDialog.setOnClickListener {
            customDialogFunction()
        }

        customProgressDialogButton.setOnClickListener {
            customProgressDialogFunction()
        }


    }

    private fun alertDialogFunction() {
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Set the title and message
        builder.setTitle("Alert")
        builder.setMessage("This is an alert Dialog")
        builder.setIcon(R.drawable.ic_alert_icon)

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(applicationContext, "You clicked Yes!", Toast.LENGTH_SHORT).show()
            // You can add more logic here if needed
            dialogInterface.dismiss()
        }

        // Set the negative button (No)
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(applicationContext, "You clicked No!", Toast.LENGTH_SHORT).show()
            // You can add more logic here if needed
            dialogInterface.dismiss()
        }

        // Set the neutral button (Cancel)
        builder.setNeutralButton("Cancel") { dialogInterface, which ->
            Toast.makeText(applicationContext, "You clicked Cancel!", Toast.LENGTH_SHORT).show()
            // You can add more logic here if needed
            dialogInterface.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun customDialogFunction() {
        // Create a Dialog instance
        val customDialog = Dialog(this)

        // Inflate the custom layout for the dialog
        //val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

        // Set the custom layout in the dialog
        customDialog.setContentView(R.layout.custom_dialog)

        // Find views inside the custom layout
        val submitButton: TextView = customDialog.findViewById(R.id.submit_button)
        val cancelButton: TextView = customDialog.findViewById(R.id.cancel_button)
        val inputField: EditText = customDialog.findViewById(R.id.input_field)
        val dialogTitle: TextView = customDialog.findViewById(R.id.dialog_title)

        // Set click listeners for TextViews acting as buttons
        submitButton.setOnClickListener {
            val inputText = inputField.text.toString()
            Toast.makeText(applicationContext, "Submitted: $inputText", Toast.LENGTH_SHORT).show()
            customDialog.dismiss() // Close the dialog after submission
        }

        cancelButton.setOnClickListener {
            Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT).show()
            customDialog.dismiss() // Close the dialog on cancel
        }

        // Show the dialog
        customDialog.show()
    }
    private fun customProgressDialogFunction() {
        // Create a Dialog instance
        val customProgressDialog = Dialog(this)
        customProgressDialog.setContentView(R.layout.custom_progress_dialog)
        customProgressDialog.show()

    }
}
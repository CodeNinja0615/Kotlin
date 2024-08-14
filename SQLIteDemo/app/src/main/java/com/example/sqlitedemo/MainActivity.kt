package com.example.sqlitedemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.R
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sqlitedemo.databinding.ActivityMainBinding
import com.example.sqlitedemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.buttonAddRecord?.setOnClickListener {
            addRecord()
        }

        setupDataIntoRecyclerView()
    }


    private fun addRecord(){
        val name = binding?.editTextName?.text.toString()
        val email = binding?.editTextEmail?.text.toString()

        val dbHandler = DatabaseHandler(this)
        if (name.isNotEmpty() && email.isNotEmpty()){
            val empModelClass = EmpModelClass(
                0,
                binding?.editTextName?.text.toString(),
                binding?.editTextEmail?.text.toString(),
            )
            val status =
                dbHandler.addEmployee(empModelClass)
            if (status>-1){
                Toast.makeText(applicationContext,"Record Added", Toast.LENGTH_LONG).show()
                binding?.editTextName?.text?.clear()
                binding?.editTextEmail?.text?.clear()

                setupDataIntoRecyclerView()
            }
        }else{
            Toast.makeText(applicationContext,"Email or Name cannot be blank", Toast.LENGTH_LONG).show()
        }
    }



    private fun getItemsList(): ArrayList<EmpModelClass>{
        val dbHandler = DatabaseHandler(this)
        val empList: ArrayList<EmpModelClass> = dbHandler.viewEmployee()
        return empList
    }
    private fun setupDataIntoRecyclerView() {

        if (getItemsList().size > 0) {
            val itemAdapter = ItemAdapter(getItemsList(),
                { emp -> updateRecordDialog(emp)},
                { emp -> deleteRecordAlertDialog(emp)}
            )
            binding?.recyclerViewRecords?.layoutManager = LinearLayoutManager(this)
            binding?.recyclerViewRecords?.adapter = itemAdapter

            binding?.recyclerViewRecords?.visibility = View.VISIBLE
            binding?.noRecord?.visibility = View.GONE
        } else {
            binding?.recyclerViewRecords?.visibility = View.GONE
            binding?.noRecord?.visibility = View.VISIBLE
        }
    }


    private fun updateRecordDialog(emp: EmpModelClass) {
        // Create a Dialog instance
        val updateDialog = Dialog(this, R.style.Theme_AppCompat_Dialog)

        updateDialog.setCancelable(false)

        // Inflate the custom layout for the dialog
        val binding = DialogUpdateBinding.inflate(layoutInflater)

        // Set the custom layout in the dialog
        updateDialog.setContentView(binding.root)

        binding.editTextUpdateName.setText(emp.name)
        binding.editTextUpdateEmail.setText(emp.email)

        // Set click listeners for TextViews acting as buttons
        binding.buttonUpdate.setOnClickListener {
            val name = binding.editTextUpdateName.text.toString()
            val email = binding.editTextUpdateEmail.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty()){
                val dbHandler = DatabaseHandler(this)
                dbHandler.updateEmployee(EmpModelClass(emp.id, name, email))

                setupDataIntoRecyclerView()

                Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_SHORT).show()

                updateDialog.dismiss()
            }else{
                Toast.makeText(applicationContext, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener {
            updateDialog.dismiss() // Close the dialog on cancel
        }

        // Show the dialog
        updateDialog.show()
    }

    private fun deleteRecordAlertDialog(emp: EmpModelClass){
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Set the title and message
        builder.setTitle("Delete Record")
//        builder.setIcon(R.drawable.ic_alert_icon)

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            val dbHandler = DatabaseHandler(this)
            dbHandler.deleteEmployee(emp)
            // You can add more logic here if needed
            Toast.makeText(applicationContext, "Record Deleted", Toast.LENGTH_SHORT).show()
            setupDataIntoRecyclerView()
            dialogInterface.dismiss()

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

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}
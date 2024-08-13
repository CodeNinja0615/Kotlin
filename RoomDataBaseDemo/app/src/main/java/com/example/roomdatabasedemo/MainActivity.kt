package com.example.roomdatabasedemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasedemo.databinding.ActivityMainBinding
import com.example.roomdatabasedemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.buttonAddRecord?.setOnClickListener {
            addRecord(employeeDao = employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect{
                val list = ArrayList(it)
                setupDataIntoRecyclerView(list, employeeDao)
            }
        }
    }

    private fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.editTextName?.text.toString()
        val email = binding?.editTextEmail?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext,"Record Saved", Toast.LENGTH_LONG).show()

                binding?.editTextName?.text?.clear()
                binding?.editTextEmail?.text?.clear()
            }
        }else{
            Toast.makeText(applicationContext,"Email or Name cannot be blank", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDataIntoRecyclerView(employeesList: ArrayList<EmployeeEntity>,
                                          employeeDao: EmployeeDao) {
        if (employeesList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeesList,
                {id-> updateRecordDialog(id,employeeDao) },
                {id-> deleteRecordAlertDialog(id,employeeDao) }
                )
            binding?.recyclerViewRecords?.layoutManager = LinearLayoutManager(this)
            binding?.recyclerViewRecords?.adapter = itemAdapter
            binding?.recyclerViewRecords?.visibility = View.VISIBLE
            binding?.noRecord?.visibility = View.GONE
        }else{
            binding?.recyclerViewRecords?.visibility = View.GONE
            binding?.noRecord?.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(id:Int, employeeDao: EmployeeDao){
        // Create a Dialog instance
        val updateDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)

        updateDialog.setCancelable(false)

        // Inflate the custom layout for the dialog
        val binding = DialogUpdateBinding.inflate(layoutInflater)

        // Set the custom layout in the dialog
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect{
                if(it != null){
                    binding.editTextUpdateName.setText(it.name)
                    binding.editTextUpdateEmail.setText(it.email)
                }
            }
        }
        // Set click listeners for TextViews acting as buttons
        binding.buttonUpdate.setOnClickListener {
            val name = binding.editTextUpdateName.text.toString()
            val email = binding.editTextUpdateEmail.text.toString()

            if(name.isNotEmpty()&&email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss() // Close the dialog after submission
                }
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

    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao){
        // Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Set the title and message
        builder.setTitle("Delete Record")
//        builder.setIcon(R.drawable.ic_alert_icon)

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext, "Record Deleted.", Toast.LENGTH_SHORT).show()
            }
            // You can add more logic here if needed
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
        if (binding!=null){
            binding = null
        }
    }

}
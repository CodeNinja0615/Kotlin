package sameer.example.coroutinesdemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var customProgressDialog : Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnExecute: Button = findViewById(R.id.button)
        btnExecute.setOnClickListener {
            customProgressDialogFunction()
            lifecycleScope.launch {
                execute("Task Executed")
            }
        }
    }

    private suspend fun execute(result: String){
        withContext(Dispatchers.IO) {
            for (i in 1..1000000) {
                Log.e("delay: ", result)
            }
            runOnUiThread {
                cancelProgressDialog()
                Toast.makeText(this@MainActivity, "Clicked", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun cancelProgressDialog(){
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private fun customProgressDialogFunction() {
        // Create a Dialog instance
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.custom_progress_dialog)
        customProgressDialog?.show()
    }
}
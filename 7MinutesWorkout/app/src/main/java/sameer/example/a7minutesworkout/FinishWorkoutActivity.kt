package sameer.example.a7minutesworkout

import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sameer.example.a7minutesworkout.databinding.ActivityFinishWorkoutBinding

class FinishWorkoutActivity : AppCompatActivity() {
    var binding: ActivityFinishWorkoutBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishWorkoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarFinishActivity)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        binding?.toolBarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.finishButton?.setOnClickListener {
//            val intent = Intent(this@FinishWorkoutActivity, MainActivity::class.java)
//            // Start the activity
//            startActivity(intent)
            finish()
        }

        val dao = (application as WorkoutApp).db.historyDao()
        addDateToDatabase(dao)
    }

    private fun addDateToDatabase(historyDao: HistoryDao){
        val c = Calendar.getInstance()
        val dateTime = c.time

        Log.e("Date: ", ""+dateTime)
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())

        val date = sdf.format(dateTime)
        Log.e("Formatted Date: ", ""+date)

        lifecycleScope.launch {
            historyDao.insert(HistoryEntity(date = date))
            Log.e("Date: ", "Added.....")
        }
    }
}
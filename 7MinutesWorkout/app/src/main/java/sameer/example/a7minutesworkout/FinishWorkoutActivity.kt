package sameer.example.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}
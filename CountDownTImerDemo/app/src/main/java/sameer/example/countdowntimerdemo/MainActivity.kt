package sameer.example.countdowntimerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button

    private var countDownTimer: CountDownTimer? = null

    private var timerDuration: Long = 60000 // 1 minute in milliseconds

    private var pauseOffset: Long = 0 // Time elapsed during pause

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         tvTimer = findViewById(R.id.textView)
         btnStart = findViewById(R.id.btn_start)
         btnPause = findViewById(R.id.btn_pause)
         btnStop = findViewById(R.id.btn_reset)

        // Display the initial timer duration
        tvTimer.text = (timerDuration / 1000).toString()

        btnStart.setOnClickListener {
            startTimer(pauseOffset)
        }
        btnPause.setOnClickListener {
            pauseTimer()
        }
        btnStop.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer(pauseOffsetL: Long) {
        // Initialize the CountDownTimer
        countDownTimer = object : CountDownTimer(timerDuration - pauseOffsetL, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the TextView with the remaining time
                pauseOffset = timerDuration - millisUntilFinished
                tvTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                // Reset the timer when finished
                Toast.makeText(this@MainActivity, "Timer is Finished.", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun pauseTimer() {
        // Cancel the timer and update the pauseOffset
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            //pauseOffset = timerDuration - tvTimer?.text.toString().toLong() * 1000
        }
    }

    private fun resetTimer() {
        // Cancel the timer, reset pauseOffset, and update the TextView
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            tvTimer.text = (timerDuration / 1000).toString()
            countDownTimer = null
            pauseOffset = 0
        }
    }
}

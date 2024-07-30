package sameer.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val mUserName = intent.getStringExtra(Constants.USER_NAME)
        val correctAnswer= intent.getIntExtra(Constants.CORRECT_ANSWERS, 0)
        val totalQuestions= intent.getIntExtra(Constants.TOTAL_QUESTION, 0)

        val tvName : TextView = findViewById(R.id.tv_name)
        val ivTrophy : ImageView = findViewById(R.id.iv_trophy)
        val tvScore : TextView = findViewById(R.id.tv_score)
        val btnFinish : Button = findViewById(R.id.btn_finish)

        tvName.text = mUserName
        tvScore.text = "Your Score is $correctAnswer out of $totalQuestions"

        btnFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}
package sameer.example.firstxmlapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnClickMe = findViewById<Button>(R.id.button01)
        val text1 = findViewById<TextView>(R.id.textView2)

        var timesClicked = 0

        btnClickMe.setOnClickListener {
         timesClicked++
            text1.text = timesClicked.toString()
            Toast.makeText(this, "Its me Sameer", Toast.LENGTH_LONG).show()
        }
    }
}
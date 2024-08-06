package sameer.example.texttospeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import sameer.example.texttospeech.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityMainBinding? = null
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Initialize the TextToSpeech engine
        tts = TextToSpeech(this, this)

        // Set up button click listener
        binding?.button?.setOnClickListener {
            val text = binding?.editTextText?.text.toString()
            if (text.isNotEmpty()) {
                speakOut(text)
            } else {
                Toast.makeText(this, "Please enter text to speak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Initialize the TextToSpeech engine
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language to US English
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                binding?.button?.isEnabled = true
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    // Function to speak the text
    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech to release resources
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        binding = null
        super.onDestroy()
    }
}

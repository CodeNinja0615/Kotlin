package sameer.example.a7minutesworkout

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import sameer.example.a7minutesworkout.databinding.ActivityExerciseBinding
import java.lang.Exception
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null

    private var restTimer: CountDownTimer? = null

    private var restProgress = 0 // Time elapsed during pause

    private var exerciseTimer: CountDownTimer? = null

    private var exerciseProgress = 0 // Time elapsed during pause

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        // Initialize the TextToSpeech engine
        tts = TextToSpeech(this, this)

        binding?.toolBarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        setUpRestView()
    }



    private fun setUpRestView(){

        try {
            val soundURI = Uri.parse(
                "android.resource://sameer.example.a7minutesworkout/" + R.raw.start_again)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setUpExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(10000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                //Toast.makeText(this@ExerciseActivity,"Here now will start the exercise.", Toast.LENGTH_LONG).show()
                currentExercisePosition++
                setUpExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(30000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 10 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                //Toast.makeText(this@ExerciseActivity,"30 Seconds are over lets go to the rest view.", Toast.LENGTH_LONG).show()
                if (currentExercisePosition < exerciseList?.size!! - 1){
                    setUpRestView()
                }else{
                    Toast.makeText(this@ExerciseActivity,"Congratulations! You have completed the 7 minutes of your workout", Toast.LENGTH_LONG).show()
                }
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        // Shutdown TextToSpeech to release resources
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        if (player != null){
            player!!.stop()
        }
        binding = null
    }

    // Initialize the TextToSpeech engine
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language to US English
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    // Function to speak the text
    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}
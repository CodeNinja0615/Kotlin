package sameer.example.a7minutesworkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import sameer.example.a7minutesworkout.databinding.ActivityExerciseBinding
import sameer.example.a7minutesworkout.databinding.DialogCustomBackConfigurationBinding
import java.lang.Exception
import java.util.Locale
import kotlin.time.times

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null

    private var restTimer: CountDownTimer? = null
    private var restTimerDuration: Long = 10
    private var restProgress = 0 // Time elapsed during pause

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseTimerDuration: Long = 60
    private var exerciseProgress = 0 // Time elapsed during pause

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding?.toolBarExercise?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }

        exerciseList = Constants.defaultExerciseList()

        // Initialize the TextToSpeech engine
        tts = TextToSpeech(this, this)

        binding?.toolBarExercise?.setNavigationOnClickListener {
            //onBackPressed()
            customDialogForBackButton()
        }
        setUpRestView()
        setupExerciseStatusRecyclerView()
    }

    private fun customDialogForBackButton() {
        // Create a Dialog instance
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfigurationBinding.inflate(layoutInflater)

        // Inflate the custom layout for the dialog
        //val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

        // Set the custom layout in the dialog
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        // Set click listeners for TextViews acting as buttons
        dialogBinding.buttonYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss() // Close the dialog after submission
        }

        dialogBinding.buttonNo.setOnClickListener {
            customDialog.dismiss() // Close the dialog on cancel
        }

        // Show the dialog
        customDialog.show()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }
    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
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

        restTimer = object: CountDownTimer(restTimerDuration*1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                //Toast.makeText(this@ExerciseActivity,"Here now will start the exercise.", Toast.LENGTH_LONG).show()
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(exerciseTimerDuration*1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 60 - exerciseProgress
                binding?.tvTimerExercise?.text = (60 - exerciseProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged()
                //Toast.makeText(this@ExerciseActivity,"30 Seconds are over lets go to the rest view.", Toast.LENGTH_LONG).show()
                if (currentExercisePosition < exerciseList?.size!! - 1){
                    setUpRestView()
                }else{
                    Toast.makeText(this@ExerciseActivity,"Congratulations! You have completed the 7 minutes of your workout", Toast.LENGTH_LONG).show()
                    finish()
                    // Create an Intent to start the FinishWorkoutActivity
                    val intent = Intent(this@ExerciseActivity, FinishWorkoutActivity::class.java)
                    // Start the activity
                    startActivity(intent)
                }
            }

        }.start()
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

}
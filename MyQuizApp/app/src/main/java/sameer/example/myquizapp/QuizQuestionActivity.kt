package sameer.example.myquizapp

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.green

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition: Int = 0

    // Declare variables for each view
    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var tvOptionOne: TextView? = null
    private var tvOptionTwo: TextView? = null
    private var tvOptionThree: TextView? = null
    private var tvOptionFour: TextView? = null
    private var btnSubmit: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        // Initialize each view by finding them by ID
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)
        btnSubmit = findViewById(R.id.btn_submit)

        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)

        mQuestionsList = Constants.getQuestions()
        setQuestion()
        defaultOptionView()


    }

    private fun setQuestion() {

//        Log.i("Questions size is ", "${questionsList.size}")
//
//        for (i in questionsList) {
//            Log.e("Questions", i.question)
//        }
        defaultOptionView()
        val question: Question = mQuestionsList!![mCurrentPosition - 1]
        ivImage?.setImageResource(question.image)
        progressBar?.progress = mCurrentPosition
        tvProgress?.text = "$mCurrentPosition/${progressBar?.max}"
        tvQuestion?.text = question.question
        tvOptionOne?.text = question.optionOne
        tvOptionTwo?.text = question.optionTwo
        tvOptionThree?.text = question.optionThree
        tvOptionFour?.text = question.optionFour

        if (mCurrentPosition == mQuestionsList!!.size){
            btnSubmit?.text = "FINISH"
        }else{
            btnSubmit?.text = "SUBMIT"
        }
    }

    private fun defaultOptionView(){
        val options = ArrayList<TextView>()
        tvOptionOne?.let {
            options.add(0, it)
        }
        tvOptionTwo?.let {
            options.add(1, it)
        }
        tvOptionThree?.let {
            options.add(2, it)
        }
        tvOptionFour?.let {
            options.add(3, it)
        }
        for(option in options){
            option.setTextColor(Color.parseColor("#FF0000"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv:TextView, selectedOptionNum:Int){
        defaultOptionView()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#FF000000"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this,
        R.drawable.selected_option_border_bg
        )
    }

    override fun onClick(view: View?){
        when(view?.id){
            R.id.tv_option_one -> {
                tvOptionOne?.let {
                    selectedOptionView(it, 1)
                }
            }
            R.id.tv_option_two -> {
                tvOptionTwo?.let {
                    selectedOptionView(it, 2)
                }
            }
            R.id.tv_option_three -> {
                tvOptionThree?.let {
                    selectedOptionView(it, 3)
                }
            }
            R.id.tv_option_four -> {
                tvOptionFour?.let {
                    selectedOptionView(it, 4)
                }
            }
            R.id.btn_submit -> {
                if (mSelectedOptionPosition == 0){
                    mCurrentPosition++
                    when{
                        mCurrentPosition <= mQuestionsList!!.size->{
                            setQuestion()
                        }
                        else->{
                            Toast.makeText(this, "Congo", Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    val question = mQuestionsList?.get(mCurrentPosition - 1)
                    if (mSelectedOptionPosition != question?.correctAnswer){
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)

                    }
                    answerView(question!!.correctAnswer, R.drawable.correct_option_border_bg)

                    if(mCurrentPosition == mQuestionsList!!.size){
                        btnSubmit?.text = "FINISH"
                    }else{
                        btnSubmit?.text = "GO TO NEXT QUESTION"
                    }
                    mSelectedOptionPosition = 0
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int){
        when(answer){
            1->{
                tvOptionOne?.background = ContextCompat.getDrawable(this, drawableView)
            }
            2->{
                tvOptionTwo?.background = ContextCompat.getDrawable(this, drawableView)
            }
            3->{
                tvOptionThree?.background = ContextCompat.getDrawable(this, drawableView)
            }
            4->{
                tvOptionFour?.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

}
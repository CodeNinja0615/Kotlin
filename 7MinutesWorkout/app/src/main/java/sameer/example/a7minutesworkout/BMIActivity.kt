package sameer.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import sameer.example.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    private var binding: ActivityBmiBinding? = null

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var currentVisibleView: String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBmiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarBMI)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
            binding?.toolBarBMI?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }


        binding?.toolBarBMI?.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId :Int ->
            if (checkedId == R.id.radioMetric){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUSUnitsView()
            }
        }




        binding?.buttonCalculate?.setOnClickListener {
            calculateUnits()
        }
    }

    private fun calculateUnits(){
        if(currentVisibleView == METRIC_UNITS_VIEW){
            if(validateMetricUnits()){
                val heightValue: Float = binding?.etHeight?.text.toString().toFloat()/100
                val weightValue: Float = binding?.etWeight?.text.toString().toFloat()

                val bmi = weightValue/(heightValue*heightValue)
                //val bmi = weightValue/Math.pow(heightValue.toDouble(),2.0)
                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Enter Your weight and height.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else {
            if (validateUSUnits()) {
                // Retrieve the input values
                val feetValue: Float = binding?.etFeet?.text.toString().toFloat()
                val inchValue: Float = binding?.etInches?.text.toString().toFloat()
                val weightValue: Float = binding?.etWeight?.text.toString().toFloat()

                // Convert weight from kg to pounds
                //val weightInPounds = weightValue * 2.20462f //----If in KG

                // Convert height to inches
                val heightInInches = (feetValue * 12) + inchValue

                // Calculate BMI using US formula
                val bmi = (weightValue / (heightInInches * heightInInches)) * 703

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Enter Your weight and height.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW

        binding?.tfHeight?.visibility = View.VISIBLE
        binding?.llUSUnits?.visibility = View.INVISIBLE
        binding?.tfWeight?.hint = "Enter Weight (kg)"

        binding?.etHeight?.text!!.clear()
        binding?.etWeight?.text!!.clear()
        binding?.llDisplayBmiResult?.visibility = View.INVISIBLE

        binding?.etFeet?.text!!.clear()
        binding?.etInches?.text!!.clear()

    }

    private fun makeVisibleUSUnitsView(){
        currentVisibleView = US_UNITS_VIEW

        binding?.llUSUnits?.visibility = View.VISIBLE
        binding?.tfHeight?.visibility = View.INVISIBLE
        binding?.tfWeight?.hint = "Enter Weight (lbs)"

        binding?.etHeight?.text!!.clear()
        binding?.etWeight?.text!!.clear()
        binding?.llDisplayBmiResult?.visibility = View.INVISIBLE

        binding?.etFeet?.text!!.clear()
        binding?.etInches?.text!!.clear()

    }
    private fun displayBMIResult(bmi: Float) {
        // Determine BMI status and display a personalized greeting
        val (bmiStatus, greetingMessage, statusColor) = when {
            bmi < 18.5 -> Triple("Underweight", "You are underweight. It's important to eat well and maintain a balanced diet!", ContextCompat.getColor(this, R.color.yellow))
            bmi in 18.5..24.9 -> Triple("Normal weight", "Great job! You have a healthy weight. Keep up the good work!", ContextCompat.getColor(this, R.color.green))
            bmi in 25.0..29.9 -> Triple("Overweight", "You are overweight. Consider a balanced diet and regular exercise.", ContextCompat.getColor(this, R.color.orange))
            else -> Triple("Obesity", "It's important to consult with a healthcare provider for personalized advice.", ContextCompat.getColor(this, R.color.red))
        }
        binding?.llDisplayBmiResult?.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIValue?.text = bmiValue //----bmi.toString() //--- THis also works

        binding?.tvBMIStatus?.text = bmiStatus
        binding?.tvBMIStatus?.setTextColor(statusColor)
        binding?.tvCongratsMessage?.text = greetingMessage
    }


    private fun validateMetricUnits():Boolean{
        var isValid = true
        if (binding?.etWeight?.text.toString().isEmpty()){
            isValid = false
        }else if(binding?.etHeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    private fun validateUSUnits():Boolean{
        var isValid = true
        if (binding?.etWeight?.text.toString().isEmpty()){
            isValid = false
        }else if(binding?.etFeet?.text.toString().isEmpty() && binding?.etInches?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
}
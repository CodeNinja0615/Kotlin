package sameer.example.unitconverter

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sameer.example.unitconverter.ui.theme.UnitConverterTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConverterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnitConverter()
                }
            }
        }
    }
}
@SuppressLint("AutoboxingStateCreation")
@Composable
fun UnitConverter(){

    var inputValue by remember{ mutableStateOf("") }
    var outputValue by remember{ mutableStateOf("") }
    var inputUnit by remember{ mutableStateOf("Meters") }
    var outputUnit by remember{ mutableStateOf("Meters") }
    var iExpanded by remember{ mutableStateOf(false) }
    var oExpanded by remember{ mutableStateOf(false) }
    val iconversionfactor = remember{ mutableStateOf(1.0) }
    val oconversionfactor = remember{ mutableStateOf(1.0) }
    //var iBtnDefault by remember { mutableStateOf("Select") }
    //var oBtnDefault by remember { mutableStateOf("Select") }

    fun convertUnit(){
        val inputValueDouble = inputValue.toDoubleOrNull() ?: 0.0
        val result = (inputValueDouble * iconversionfactor.value * 100/oconversionfactor.value)/*.roundToInt()*/ / 100
        outputValue = result.toString()
    }


    Column(modifier =  Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        // Here all the UI elements will be stacked below each other
        Text("Unit Converter", modifier = Modifier.padding(16.dp),style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = inputValue,
            onValueChange = {
            inputValue = it
            convertUnit()
            // Here goes what happens when text field value changes
            },
           label = {Text("Enter Value")}
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
//            val context = LocalContext.current
//            // Here all the UI elements will be stacked next to each other
//            Button(onClick = {Toast.makeText(context,
//                    "Thanks for clicking",
//                    Toast.LENGTH_LONG).show()})
//            {
//                Text("Click Me!")
//                Icon(Icons.Default.Check, contentDescription = "")
//            }
            //-----Input Box
            Box{
                //----Input Button
                Button(onClick = { iExpanded = true }) {
                    Text(inputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Drop-Down Button")
                }
                DropdownMenu(expanded = iExpanded, onDismissRequest = { iExpanded = false }) {
                    DropdownMenuItem(text = { Text("Centimeters")},
                        onClick = {
                        inputUnit = "Centimeters"
                        iExpanded = false
                        iconversionfactor.value = 0.01
                        convertUnit()
                    })
                    DropdownMenuItem(text = { Text("Meters")},
                        onClick = {
                            inputUnit = "Meters"
                            iExpanded = false
                            iconversionfactor.value = 1.0
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Feet")},
                        onClick = {
                            inputUnit = "Feet"
                            iExpanded = false
                            iconversionfactor.value = 0.3048
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Millimeters")},
                        onClick = {
                            inputUnit = "Millimeters"
                            iExpanded = false
                            iconversionfactor.value = 0.001
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Inches")},
                        onClick = {
                            inputUnit = "Inches"
                            iExpanded = false
                            iconversionfactor.value = 0.0253
                            convertUnit()
                        })
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(onClick = {
                    val thirdUnit = inputUnit
                    inputUnit = outputUnit
                    outputUnit = thirdUnit

                    val thirdFactor = iconversionfactor.value
                    iconversionfactor.value = oconversionfactor.value
                    oconversionfactor.value =thirdFactor
                    convertUnit()
                }) {
                    Row {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription ="" )
                        Icon(Icons.Default.KeyboardArrowRight,contentDescription = "")
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            //-----Output Box
            Box {
                //----Output Button
                Button(onClick = { oExpanded = true}) {
                    Text(outputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select drop-Down Button")
                }
                DropdownMenu(expanded = oExpanded, onDismissRequest = { oExpanded = false }) {
                    DropdownMenuItem(text = { Text("Centimeters")},
                        onClick = {
                            outputUnit = "Centimeters"
                            oExpanded = false
                            oconversionfactor.value = 0.01
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Meters")},
                        onClick = {
                            outputUnit = "Meters"
                            oExpanded = false
                            oconversionfactor.value = 1.0
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Feet")},
                        onClick = {
                            outputUnit = "Feet"
                            oExpanded = false
                            oconversionfactor.value = 0.3048
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Millimeters")},
                        onClick = {
                            outputUnit = "Millimeters"
                            oExpanded = false
                            oconversionfactor.value = 0.001
                            convertUnit()
                        })
                    DropdownMenuItem(text = { Text("Inches")},
                        onClick = {
                            outputUnit = "Inches"
                            oExpanded = false
                            oconversionfactor.value = 0.0253
                            convertUnit()
                        })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Result: $outputValue$outputUnit",
            style = MaterialTheme.typography.headlineMedium
            )
    }
}


@Preview(showBackground = true)
@Composable
fun UnitConverterPreview() {
    UnitConverterTheme {
        UnitConverter()
    }
}
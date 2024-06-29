package sameer.example.navigationsample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sameer.example.navigationsample.ui.theme.NavigationSampleTheme


@Composable
fun FirstScreen(navigationToSecondScreen: (String,String) -> Unit){
    val name = remember{ mutableStateOf("") }
    val age = remember{ mutableStateOf("") }

    if (name.value.isBlank()) {name.value = "no name"}
    if (age.value.isBlank()) {age.value = "0"}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "This is the First Screen", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name.value, onValueChange = {
            name.value = it
        }, label = { Text(text = "name")})

        OutlinedTextField(value = age.value, onValueChange = {
            age.value = it
        }, label = { Text(text = "age")})

        Button(onClick = {
            navigationToSecondScreen(name.value, age.value)
        })
        {
            Text(text = "Go to next screen")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FirstScreenPreview(){
    NavigationSampleTheme {
        //FirstScreen(){"Sameer"; "18"}
    }
}
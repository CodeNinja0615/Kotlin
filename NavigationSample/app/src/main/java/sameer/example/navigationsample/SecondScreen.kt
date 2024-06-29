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
fun SecondScreen(name :String, age:Int, navigationToFirstScreen: () ->Unit, navigationToThirdScreen: () -> Unit){
    //val name = remember{ mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "This is the Second Screen", fontSize = 24.sp)
        Text(text = "Welcome $name you are $age years old", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(value = name.value, onValueChange = {
//            name.value = it
//        }, label = { Text(text = "Second Screen")})
        Button(onClick = {
            navigationToFirstScreen()
        })
        {
            Text(text = "Go to First screen")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navigationToThirdScreen()
        })
        {
            Text(text = "Go to Third screen")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SecondScreenPreview(){
    NavigationSampleTheme {
        SecondScreen("Sameer", 15,navigationToFirstScreen = {}, navigationToThirdScreen = {})
    }
}
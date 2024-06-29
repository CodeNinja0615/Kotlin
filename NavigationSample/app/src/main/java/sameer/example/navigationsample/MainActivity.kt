package sameer.example.navigationsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sameer.example.navigationsample.ui.theme.NavigationSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}



@Composable
fun MyApp(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "firstscreen"){
        composable(route = "firstscreen"){
            FirstScreen() {name, age ->
                navController.navigate(route = "secondscreen/$name/$age")
            }
        }
        composable(route = "secondscreen/{name}/{age}"){
            val name = it.arguments?.getString("name") ?: "no name"
            val age = it.arguments?.getString("age") ?: "no name"
            SecondScreen (name = name, age = age.toInt() ,
                navigationToFirstScreen = {navController.navigate(route = "firstscreen")},
                navigationToThirdScreen = {navController.navigate(route = "thirdscreen")}
            )
        }
        composable(route = "thirdscreen"){
            ThirdScreen {
                navController.navigate(route = "firstscreen")
            }
        }
    }
}
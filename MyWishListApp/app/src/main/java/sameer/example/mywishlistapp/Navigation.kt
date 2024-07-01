package sameer.example.mywishlistapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sameer.example.mywishlistapp.ui.theme.MyWishListAppTheme

@Composable
fun Navigation(viewModel: WishViewModel = viewModel(),
               navController: NavHostController = rememberNavController())
{
    NavHost(navController = navController , startDestination = Screen.HomeScreen.route ){
        composable(route = Screen.HomeScreen.route ){
            HomeView(navController, viewModel)
        }
        composable(route = Screen.AddScreen.route ){
            AddEditDetailView(id = 0L, viewModel = viewModel, navController = navController)
        }
    }
}

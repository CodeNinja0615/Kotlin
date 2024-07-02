package sameer.example.mywishlistapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import sameer.example.mywishlistapp.ui.theme.MyWishListAppTheme

@Composable
fun Navigation(viewModel: WishViewModel = viewModel(),
               navController: NavHostController = rememberNavController())
{
    NavHost(navController = navController , startDestination = Screen.HomeScreen.route ){
        composable(route = Screen.HomeScreen.route ){
            HomeView(navController, viewModel)
        }
        composable(route = Screen.AddScreen.route + "/{id}",
            arguments = listOf
                (
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
                )
        ){
            entry->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditDetailView(id = id, viewModel = viewModel, navController = navController)
        }
    }
}

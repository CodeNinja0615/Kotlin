package sameer.example.musicappui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sameer.example.musicappui.ui.theme.AccountView
import sameer.example.musicappui.ui.theme.Browse
import sameer.example.musicappui.ui.theme.Home
import sameer.example.musicappui.ui.theme.Library
import sameer.example.musicappui.ui.theme.Subscription

@Composable
fun Navigation(navController: NavController, viewModel: MainViewModel, pd: PaddingValues){

    NavHost(navController = navController as NavHostController,
        startDestination = Screen.DrawerScreen.Account.route,
        modifier = Modifier.padding(pd)
    )
    {
//        composable(Screen.DrawerScreen.AddAccount.route){  //----- Not a screen just an alert box
//
//        }
        composable(Screen.DrawerScreen.Subscription.route){
            Subscription()
        }
        composable(Screen.DrawerScreen.Account.route){
            AccountView()
        }
        composable(Screen.BottomScreen.Home.route){
            Home()
        }
        composable(Screen.BottomScreen.Library.route){
            Library()
        }
        composable(Screen.BottomScreen.Browse.route){
            Browse()
        }
    }

}
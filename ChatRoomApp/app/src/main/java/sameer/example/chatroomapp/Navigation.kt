package sameer.example.chatroomapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sameer.example.chatroomapp.screen.LoginScreen
import sameer.example.chatroomapp.screen.Screen
import sameer.example.chatroomapp.screen.SignUpScreen
import sameer.example.chatroomapp.viewmodel.AuthViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(authViewModel= authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onSignInSuccess = { navController.navigate(Screen.ChatRoomsScreen.route)}
            )
        }
        composable(Screen.ChatRoomsScreen.route) {
        }
    }
}
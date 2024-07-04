package sameer.example.musicappui

import androidx.annotation.DrawableRes

sealed class Screen(val title: String, val route: String) {
    sealed class BottomScreen(val bTitle: String,val bRoute:String, @DrawableRes val icon: Int)
        :Screen(bTitle,bRoute){
        object Home:BottomScreen(
            "Home",
            "home",
            R.drawable.ic_baseline_home_24
        )
        object Library: BottomScreen(
            "Library",
            "library",
            R.drawable.ic_baseline_library_music_24
        )
        object Browse: BottomScreen(
            "Browse",
            "browse",
            R.drawable.ic_baseline_apps_24
        )
    }

    sealed class DrawerScreen(val dTitle: String,val dRoute:String, @DrawableRes val icon: Int)
        :Screen(dTitle,dRoute){
        object Account:DrawerScreen(
            "Account",
            "account",
            R.drawable.ic_account
        )
        object Subscription: DrawerScreen(
            "Subscription",
            "subscription",
            R.drawable.ic_subscribe
        )
        object AddAccount: DrawerScreen(
        "Add Account",
        "add_account",
        R.drawable.baseline_person_add_alt_1_24
        )
    }
}


val screensInDrawer = listOf(
    Screen.DrawerScreen.Account,
    Screen.DrawerScreen.Subscription,
    Screen.DrawerScreen.AddAccount
)
val screensInBottom = listOf(
    Screen.BottomScreen.Home,
    Screen.BottomScreen.Library,
    Screen.BottomScreen.Browse
)
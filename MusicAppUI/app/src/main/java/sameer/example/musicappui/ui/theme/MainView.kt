package sameer.example.musicappui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import sameer.example.musicappui.ui.theme.AccountDialog
import sameer.example.musicappui.MainViewModel
import sameer.example.musicappui.Navigation
import sameer.example.musicappui.Screen
import sameer.example.musicappui.screensInDrawer


@Composable
fun MainView(){

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel:MainViewModel = viewModel()

    //---- Allow us to find us on which screen we currently are
    val controller: NavController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val dialogOpen = remember{
        mutableStateOf(false)
    }
    val currentScreen = remember{
        viewModel.currentScreen.value
    }
    val title = remember{
        //--- Change that currentScreen.title
        mutableStateOf(currentScreen.title)
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = title.value)},
                navigationIcon = {
                    IconButton(onClick = {
                    //----TODO Open the drawer
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                     })
                    {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null )
                    }
                })
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            LazyColumn(modifier = Modifier.padding(16.dp)){
                items(screensInDrawer){
                    item ->
                    DrawerItem(selected = currentRoute == item.dRoute, item = item) {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        if (item.dRoute == "add_account"){
                            //---TODO open Dialog
                            dialogOpen.value = true
                        }else{
                            controller.navigate(item.dRoute)
                            title.value = item.dTitle
                        }
                    }
                }
            }
        }
    ) {
        Navigation(navController = controller, viewModel = viewModel, pd = it )
        AccountDialog(dialogOpen = dialogOpen)
    }
}



@Composable
fun DrawerItem(selected: Boolean,
               item: Screen.DrawerScreen,
               onDrawerItemClicked:()-> Unit
               )
{
    val background = if(selected) Color.DarkGray else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .background(background)
            .clickable {
                onDrawerItemClicked()
            }
    )
    {
     Icon(painter = painterResource(id = item.icon),
         contentDescription = item.dTitle,
         modifier = Modifier.padding(end = 8.dp, top = 4.dp)
     )
        Text(
            text = item.dTitle,
            style = MaterialTheme.typography.h5
        )
    }
}

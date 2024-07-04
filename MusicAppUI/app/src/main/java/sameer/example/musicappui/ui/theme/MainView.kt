package sameer.example.musicappui.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
import sameer.example.musicappui.R
import sameer.example.musicappui.Screen
import sameer.example.musicappui.screensInBottom
import sameer.example.musicappui.screensInDrawer
import sameer.example.musicappui.ui.theme.MusicAppUITheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainView(){

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel:MainViewModel = viewModel()
    val isSheetFullScreen by remember { mutableStateOf(false) }
    val modifier = if(isSheetFullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
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
    
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {it != ModalBottomSheetValue.HalfExpanded}
        )

    val roundedCornerRadius = if (isSheetFullScreen) 0.dp else 12.dp

    val bottomBar: @Composable () -> Unit = {
        if (currentScreen is Screen.DrawerScreen || currentScreen is Screen.BottomScreen.Home){
            BottomNavigation(modifier = Modifier.wrapContentSize()) {
                screensInBottom.forEach{
                    item->
//                    val isSelected = currentRoute == item.bRoute //------UI Properly working without these No need
//                    Log.d("Navigation","Item: ${item.bTitle}, Current Route: ${currentRoute}, Is Selected: $isSelected")   //------UI Properly working without these No need
//                    val tint = if (isSelected)Color.White else Color.DarkGray   //------UI Properly working without these No need
                    BottomNavigationItem(selected = currentRoute == item.bRoute,
                        onClick = {
                            controller.navigate(item.bRoute)
                            title.value = item.bTitle
                        },
                        icon = {
                            Icon(painter = painterResource(id = item.icon),
                                contentDescription = item.bTitle,
//                                tint = tint    //------UI Properly working without these No need
                                )
                        },
                        label = {
                            Text(
                                text = item.bTitle,
//                                color = tint    //------UI Properly working without these No need
                            )
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Black
                    )
                }
            }
        }
    }


    ModalBottomSheetLayout(sheetContent = {
        MoreBottomSheet(modifier = modifier)
    },
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = title.value)},
                    actions = {
                              IconButton(onClick = {
                                  scope.launch {
                                      if (modalSheetState.isVisible){
                                          modalSheetState.hide()
                                      }else{
                                          modalSheetState.show()
                                      }
                                  }
                              }) {
                                  Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                              }
                    },
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
            bottomBar = bottomBar,
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


@Composable
fun MoreBottomSheet(modifier: Modifier){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .background(MaterialTheme.colors.primarySurface)){
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                    contentDescription = "Settings"
                )
                Text(text = "Settings")
            }
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_share_24),
                    contentDescription = "Share"
                )
                Text(text = "Share")
            }
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_help_24),
                    contentDescription = "Help"
                )
                Text(text = "Help")
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun MainViewPreview(){
    MusicAppUITheme {
        MainView()
    }
}
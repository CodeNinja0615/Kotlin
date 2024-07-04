package sameer.example.musicappui.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sameer.example.musicappui.Lib
import sameer.example.musicappui.R
import sameer.example.musicappui.libraraies

@Composable
fun Library(){
    LazyColumn(){
        items(libraraies){
            lib->
            LibraryItem(lib = lib)
        }
    }
}

@Composable
fun LibraryItem(lib: Lib){
    Column() {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                Icon(painter = painterResource(id = lib.icon),
                    contentDescription = lib.name,
                    modifier = Modifier.padding(horizontal = 8.dp) )
                Text(text = lib.name)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
        Divider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp), color = Color.LightGray)
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryPreview(){
    MusicAppUITheme {
        Library()
    }
}
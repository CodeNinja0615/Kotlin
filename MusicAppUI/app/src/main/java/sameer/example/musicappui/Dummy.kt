package sameer.example.musicappui

import androidx.annotation.DrawableRes

data class Lib(@DrawableRes val icon: Int, val name:String)

val libraraies = listOf<Lib>(
    Lib(R.drawable.ic_baseline_playlist_play_24,"Playlist"),
    Lib(R.drawable.ic_baseline_mode_24,"Artist"),
    Lib(R.drawable.ic_baseline_album_24,"Album"),
    Lib(R.drawable.ic_baseline_music_note_24,"Songs"),
    Lib(R.drawable.ic_baseline_menu_open_24,"Genre"),
)

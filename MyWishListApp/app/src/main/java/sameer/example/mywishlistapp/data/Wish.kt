package sameer.example.mywishlistapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wish-table")
data class Wish(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name="wish-title")
    val title: String = "",
    @ColumnInfo(name="wish-desc")
    val description: String = ""
)

object DummyWish{
    val wishList = listOf(
        Wish(title = "Google Watch", description = "A Google watch for a pro"),
        Wish(title = "Apple Watch", description = "An Apple watch for a pro"),
        Wish(title = "Samsung Watch", description = "A Samsung watch for a pro"),
        Wish(title = "Fastrack Watch", description = "A Fastrack watch for a pro")
    )
}
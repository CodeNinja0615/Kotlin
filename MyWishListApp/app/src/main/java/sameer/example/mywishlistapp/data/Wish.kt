package sameer.example.mywishlistapp.data

data class Wish(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    //val isEditing: Boolean = false
)

object DummyWish{
    val wishList = listOf(
        Wish(title = "Google Watch", description = "A Google watch for a pro"),
        Wish(title = "Apple Watch", description = "An Apple watch for a pro"),
        Wish(title = "Samsung Watch", description = "A Samsung watch for a pro"),
        Wish(title = "Fastrack Watch", description = "A Fastrack watch for a pro")
    )
}
package sameer.example.chatroomapp.data

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "" //--- Here I am putting Password in list which most people don't
)
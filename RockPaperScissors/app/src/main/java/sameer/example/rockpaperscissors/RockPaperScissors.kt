@file:Suppress("NAME_SHADOWING")

package sameer.example.rockpaperscissors

fun main(){
    println("Rock, Paper or Scissors? Enter your choice")
    val playerChoice: String = readln()
    rockpaperScissors(playerChoice)
}

fun rockpaperScissors(playerChoice: String){
    lateinit var computerChoice : String
    var playerChoice: String = playerChoice
    val arr = arrayOf("Rock", "Paper", "Scissors")
//    val new = arrayOf(1,2,3).random()
    while(playerChoice in arr ) {
        when ((1..3).random()) {
            1 -> computerChoice = "Rock"
            2 -> computerChoice = "Paper"
            3 -> computerChoice = "Scissors"
        }
        println("Player's choice is $playerChoice")
        println("Computer's choice is $computerChoice")
        //Using else if
        //    if (computerChoice == playerChoice) println("Its a tie")
        //    else if (computerChoice == "Rock" && playerChoice == "Paper") println("You win")
        //    else if (computerChoice == "Rock" && playerChoice == "Scissors") println("You lose")
        //    else if (computerChoice == "Paper" && playerChoice == "Rock") println("You lose")
        //    else if (computerChoice == "Paper" && playerChoice == "Scissors") println("You win")
        //    else if (computerChoice == "Scissors" && playerChoice == "Rock") println("You win")
        //    else if (computerChoice == "Scissors" && playerChoice == "Paper") println("You lose")

        //--Using when
        val winner = when {
            computerChoice == "Rock" && playerChoice == "Paper" -> "you win"
            computerChoice == "Rock" && playerChoice == "Scissors" -> "You lose"
            computerChoice == "Paper" && playerChoice == "Rock" -> "You lose"
            computerChoice == "Paper" && playerChoice == "Scissors" -> "you win"
            computerChoice == "Scissors" && playerChoice == "Rock" -> "you win"
            computerChoice == "Scissors" && playerChoice == "Paper" -> "You lose"
            else -> {"It's a tie"}
        }
        println(winner)
        println("Rock, Paper or Scissors? Enter your choice")
        playerChoice = readln()
    }
    println("$playerChoice is a wrong Input")
    main()
}
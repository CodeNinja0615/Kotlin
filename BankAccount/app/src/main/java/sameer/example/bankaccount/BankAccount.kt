package sameer.example.bankaccount

data class BankAccount(var accountHolder: String, var balance:Double, var debitCard:Boolean, var cardNumber:Int?) {

    private val transactionHistory = mutableListOf<String>()


    fun depositMoney(amount:Double){
        balance+=amount
        transactionHistory.add("$accountHolder deposited $$amount")
    }

    fun withdrawMoney(amount: Double, through: String) {
        if(debitCard) {
            if (amount <= balance && through == "Card") {
                transactionHistory.add("$accountHolder withdrew $$amount from Card Number: $cardNumber")
            } else {
                println("account balance is low")
            }
        }
        else println("No card no money")
    }

    fun displayTransactionHistory(){
        println("Transaction history for $accountHolder")
        for (transaction in transactionHistory){
            println(transaction)
        }
    }
}

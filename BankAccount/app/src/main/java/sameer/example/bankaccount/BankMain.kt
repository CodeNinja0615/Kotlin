package sameer.example.bankaccount

fun main(){

    val sameerBankAccount = BankAccount("Sameer",9635.00,true,6969858)
    val masoodBankAccount = BankAccount("Masood",9635.00,true,3433211)
    val henaBankAccount = BankAccount("Hena",9635.00,true,65450212)
    val annieBankAccount = BankAccount("Annie",9635.00,true,33431233)
    val accData = mutableListOf(
        sameerBankAccount,masoodBankAccount,henaBankAccount,annieBankAccount
    )
    sameerBankAccount.depositMoney(9586.20)
    sameerBankAccount.withdrawMoney(19000.00, "Card")
    sameerBankAccount.depositMoney(852.0)
    sameerBankAccount.withdrawMoney(562.00, "Card")
    sameerBankAccount.displayTransactionHistory()


    println(sameerBankAccount.balance)


    val garouBankAccount = BankAccount("Garou",96351.00,false,null)

    accData.add(garouBankAccount)

    println(accData)
    println(accData.filter { it.accountHolder=="Annie" })

    println(accData.map { it.balance})
    garouBankAccount.withdrawMoney(523.00,"Card")

    //accData.set(0,BankAccount("Sameer Akhtar",9635.00,true,6969858))
    accData[0] = BankAccount("Sameer Akhtar",9635.00,true,6969858)
    accData[1].accountHolder = "Masood Akhtar"
    accData.set(2,BankAccount("Hena Rizvi",9635.00,true,65450212))
    println(accData)

    println("\n")

    for (details in accData){
        println(details)
    }

    for (i in 0 until accData.size){
        println("${accData[i].accountHolder} has $${accData[i].balance} balance")
    }

}
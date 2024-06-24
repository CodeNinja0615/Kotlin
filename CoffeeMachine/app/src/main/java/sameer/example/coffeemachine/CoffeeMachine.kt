package sameer.example.coffeemachine

fun main() {
    val theData: MutableList<CoffeeData> = makeCoffee()
    println(theData)


    theData.add(CoffeeData("Sammy",1,"Latte"))
    println(theData)
    println(theData.filter { it.name == "Sammy"  })
    //theData.removeAt(0)
    theData[0]=CoffeeData("Akhtar",1,"Latte")
    println(theData)
    theData.set(1, CoffeeData("King Garou",2,"Cappuccino"))
    println(theData)


    println(theData.contains(CoffeeData("Garou", 0, "Espresso")))
    println(theData.filter { it.coffee == "Cappuccino" })

    println(theData[0].sugar)

    for (i in theData.map { it.name }){
        println(i)
    }
    println("\n")
    for (i in 0 until theData.size){
        println(theData[i].coffee)
    }
    theData[0] = CoffeeData("Sameer",0,"Latte")
    theData[0].name = "Sameer Akthar"
}

data class CoffeeData(var name: String, val sugar: Int, val coffee: String)

fun makeCoffee() : MutableList<CoffeeData> {
    val data = mutableListOf<CoffeeData>(
        CoffeeData("Sameer",1,"Latte"),
        CoffeeData("King",2,"Cappuccino"),
        CoffeeData("Garou",0,"Espresso"),
        CoffeeData("Garou",8,"Mocha")
    )
    return data
}

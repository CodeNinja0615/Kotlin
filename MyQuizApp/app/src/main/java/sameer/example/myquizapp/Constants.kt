package sameer.example.myquizapp

object Constants {
    fun getQuestions(): ArrayList<Question>{
        val questionsList = ArrayList<Question>()

        val que1 = Question(
            1, "What country this flag belongs to?",
            R.drawable.backgroung_img,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que1)

        val que2 = Question(
            1, "What country this flag belongs to?",
            R.drawable.backgroung_img,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que2)
        val que3 = Question(
            1, "What country this flag belongs to?",
            R.drawable.backgroung_img,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que3)
        val que4 = Question(
            1, "What country this flag belongs to?",
            R.drawable.backgroung_img,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que4)
        val que5 = Question(
            1, "What country this flag belongs to?",
            R.drawable.backgroung_img,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que5)

        return questionsList
    }
}
package sameer.example.myquizapp

object Constants {

    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTION:String = "total_questions"
    const val CORRECT_ANSWERS:String = "correct_answers"


    fun getQuestions(): ArrayList<Question>{
        val questionsList = ArrayList<Question>()

        val que1 = Question(
            1, "What country this flag belongs to?",
            R.drawable.russia,"Argentina","Australia",
            "India", "Austria", 3
        )
        questionsList.add(que1)

        val que2 = Question(
            1, "What country this flag belongs to?",
            R.drawable.flag2,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que2)
        val que3 = Question(
            1, "What country this flag belongs to?",
            R.drawable.flag3,"Argentina","Australia",
            "India", "Austria", 2
        )
        questionsList.add(que3)
        val que4 = Question(
            1, "What country this flag belongs to?",
            R.drawable.china,"Argentina","Australia",
            "India", "Austria", 1
        )
        questionsList.add(que4)
        val que5 = Question(
            1, "What country this flag belongs to?",
            R.drawable.canada,"Argentina","Australia",
            "India", "Austria", 4
        )
        questionsList.add(que5)

        return questionsList
    }
}
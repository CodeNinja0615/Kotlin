package sameer.example.a7minutesworkout

object Constants {
    fun defaultExerciseList(): ArrayList<ExerciseModel>{
        val exerciseList = ArrayList<ExerciseModel>()

        val jumpingJacks = ExerciseModel(
            1,
            "Jumping Jacks",
            R.drawable.ic_jumping_jack,
            false,
            false
        )
        exerciseList.add(jumpingJacks)

        val lunges = ExerciseModel(
            2,
            "Lunges",
            R.drawable.ic_lunge,
            false,
            false
        )
        exerciseList.add(lunges)

        val pushUp = ExerciseModel(
            3,
            "Push Up",
            R.drawable.ic_pushup,
            false,
            false
        )
        exerciseList.add(pushUp)

        val sidePlank = ExerciseModel(
            4,
            "Side Plank",
            R.drawable.ic_side_plank,
            false,
            false
        )
        exerciseList.add(sidePlank)

        val squat = ExerciseModel(
            5,
            "Squats",
            R.drawable.ic_squat,
            false,
            false
        )
        exerciseList.add(squat)

        val stretch = ExerciseModel(
            6,
            "Stretching",
            R.drawable.ic_stretch,
            false,
            false
        )
        exerciseList.add(stretch)

        val triceps = ExerciseModel(
            7,
            "Triceps",
            R.drawable.ic_triceps,
            false,
            false
        )
        exerciseList.add(triceps)


        return exerciseList
    }
}
package com.example.studentdashboard.models

data class TimeTable( //--- This will take in all the images from the FireStore for TimeTable
    val classTimeTable: String = "",
    val midTerm: String = "",
    val finalTerm: String = ""
)

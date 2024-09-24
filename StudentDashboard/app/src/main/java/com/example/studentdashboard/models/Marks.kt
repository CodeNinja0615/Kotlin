package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable
import android.text.Editable

data class Marks(
    val term: String = "",
    val english: Double =  0.0,
    val maths: Double = 0.0,
    val science: Double = 0.0,
    val socialScience: Double = 0.0,
    val hindi: Double = 0.0,
    val computerScience: Double = 0.0,
    var percentage: Double = 0.0,
    var cgpa: Double = 0.0,
    var grade: String = "",
    val imageResult: String = "",
    var status: String = "",
    val rank: Int = 0,
    val updateFlag: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(term)
        parcel.writeDouble(english)
        parcel.writeDouble(maths)
        parcel.writeDouble(science)
        parcel.writeDouble(socialScience)
        parcel.writeDouble(hindi)
        parcel.writeDouble(computerScience)
        parcel.writeDouble(percentage)
        parcel.writeDouble(cgpa)
        parcel.writeString(grade)
        parcel.writeString(imageResult)
        parcel.writeString(status)
        parcel.writeInt(rank)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Marks> {
        override fun createFromParcel(parcel: Parcel): Marks {
            return Marks(parcel)
        }

        override fun newArray(size: Int): Array<Marks?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class ClassRoom( //--- This will take in all the images from the FireStore for TimeTable
    val classTimeTable: String = "",
    val midTerm: String = "",
    val finalTerm: String = "",
    val notice: ArrayList<Notice> = ArrayList(),
    val content: ArrayList<PdfFile> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Notice.CREATOR)!!,
        parcel.createTypedArrayList(PdfFile.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(classTimeTable)
        parcel.writeString(midTerm)
        parcel.writeString(finalTerm)
        parcel.writeTypedList(notice)
        parcel.writeTypedList(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassRoom> {
        override fun createFromParcel(parcel: Parcel): ClassRoom {
            return ClassRoom(parcel)
        }

        override fun newArray(size: Int): Array<ClassRoom?> {
            return arrayOfNulls(size)
        }
    }
}

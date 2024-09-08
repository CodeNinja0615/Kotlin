package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class School (
    val schoolImage: String = "",
    val notice: ArrayList<String> = ArrayList(),
//    val post: ArrayList<String> = ArrayList(),
    val images: ArrayList<SchoolImages> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
//        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(SchoolImages.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(schoolImage)
        parcel.writeStringList(notice)
//        parcel.writeStringList(post)
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<School> {
        override fun createFromParcel(parcel: Parcel): School {
            return School(parcel)
        }

        override fun newArray(size: Int): Array<School?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class SchoolImages(
    val title: String = "",
    val image: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SchoolImages> {
        override fun createFromParcel(parcel: Parcel): SchoolImages {
            return SchoolImages(parcel)
        }

        override fun newArray(size: Int): Array<SchoolImages?> {
            return arrayOfNulls(size)
        }
    }
}
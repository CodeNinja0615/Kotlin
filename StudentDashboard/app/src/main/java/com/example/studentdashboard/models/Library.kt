package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class Library(
    val title: String = "",
    val publisher: String = "",
    val description: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Library> {
        override fun createFromParcel(parcel: Parcel): Library {
            return Library(parcel)
        }

        override fun newArray(size: Int): Array<Library?> {
            return arrayOfNulls(size)
        }
    }
}
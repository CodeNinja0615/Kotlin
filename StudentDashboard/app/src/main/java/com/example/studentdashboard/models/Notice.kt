package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class Notice (
    val title: String = "",
    val createdBy: String = "",
    val notice: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeString(notice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notice> {
        override fun createFromParcel(parcel: Parcel): Notice {
            return Notice(parcel)
        }

        override fun newArray(size: Int): Array<Notice?> {
            return arrayOfNulls(size)
        }
    }
}
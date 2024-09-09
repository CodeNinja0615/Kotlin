package com.example.studentdashboard.models

import android.os.Parcel
import android.os.Parcelable

data class PdfFile(
    val name: String,
    val url: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PdfFile> {
        override fun createFromParcel(parcel: Parcel): PdfFile {
            return PdfFile(parcel)
        }

        override fun newArray(size: Int): Array<PdfFile?> {
            return arrayOfNulls(size)
        }
    }
}

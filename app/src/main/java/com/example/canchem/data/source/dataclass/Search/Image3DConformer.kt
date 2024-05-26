package com.example.canchem.data.source.dataclass.Search

import com.google.gson.annotations.SerializedName
import android.os.Parcel
import android.os.Parcelable


data class Image3DConformer(
    @SerializedName("bonds") val bonds: List<Int>, //결합 정보 리스트
    @SerializedName("coords") val coords: List<Double> //3D 좌표 리스트
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createIntArray()?.toList() ?: emptyList(),
        parcel.createDoubleArray()?.toList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeIntArray(bonds.toIntArray())
        parcel.writeDoubleArray(coords.toDoubleArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image3DConformer> {
        override fun createFromParcel(parcel: Parcel): Image3DConformer {
            return Image3DConformer(parcel)
        }

        override fun newArray(size: Int): Array<Image3DConformer?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.canchem.data.source.dataclass.Search

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ChemicalCompound(
    @SerializedName("id") val id: String,
    @SerializedName("cid") val cid: Int,
    @SerializedName("inpac_name") val inpacName: String,
    @SerializedName("molecular_formula") val molecularFormula: String,
    @SerializedName("molecular_weight") val molecularWeight: Double,
    @SerializedName("isomeric_smiles") val isomericSmiles: String,
    @SerializedName("inchi") val inchi: String,
    @SerializedName("inchiKey") val inchiKey: String,
    @SerializedName("canonical_smiles") val canonicalSmiles: String,
    @SerializedName("description") val description: String,
    @SerializedName("image_2D_url") val image2DUri: String?,
    @SerializedName("image_3D_conformer") val image3DConformer: Image3DConformer?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readParcelable(Image3DConformer::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(cid)
        parcel.writeString(inpacName)
        parcel.writeString(molecularFormula)
        parcel.writeDouble(molecularWeight)
        parcel.writeString(isomericSmiles)
        parcel.writeString(inchi)
        parcel.writeString(inchiKey)
        parcel.writeString(canonicalSmiles)
        parcel.writeString(description)
        parcel.writeString(image2DUri)
        parcel.writeParcelable(image3DConformer, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChemicalCompound> {
        override fun createFromParcel(parcel: Parcel): ChemicalCompound {
            return ChemicalCompound(parcel)
        }

        override fun newArray(size: Int): Array<ChemicalCompound?> {
            return arrayOfNulls(size)
        }
    }
}

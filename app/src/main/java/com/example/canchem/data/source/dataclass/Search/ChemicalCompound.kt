package com.example.canchem.data.source.dataclass.Search

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
)

package com.example.canchem.data.source

import com.google.gson.annotations.SerializedName

data class mol_Name(
    @SerializedName("cid") val cid: Int,
    @SerializedName("inpac_name") val inpacName: String,
    @SerializedName("molecular_formula") val molecularFormula: String,
    @SerializedName("molecular_weight") val molecularWeight: String,
    @SerializedName("isomeric_smlies") val isomericSmlies: String,
    @SerializedName("inchi") val inchi: String,
    @SerializedName("inchiKey") val inchiKey: String,
    @SerializedName("canonical_smlies") val canonicalSmlies: String
)

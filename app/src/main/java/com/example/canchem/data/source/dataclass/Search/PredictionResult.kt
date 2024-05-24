package com.example.canchem.data.source.dataclass.Search

import com.google.gson.annotations.SerializedName

data class PredictionResult(
    @SerializedName("user_token")
    val userToken: String,

    @SerializedName("smiles")
    val smiles: String
)

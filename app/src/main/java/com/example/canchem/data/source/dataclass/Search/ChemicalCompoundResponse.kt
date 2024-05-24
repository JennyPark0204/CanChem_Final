package com.example.canchem.data.source.dataclass.Search

import com.google.gson.annotations.SerializedName

data class ChemicalCompoundResponse(
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("searchResults") val searchResults: ArrayList<ChemicalCompound>
)

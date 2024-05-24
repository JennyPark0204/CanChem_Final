package com.example.canchem.data.source.dataclass

import com.example.canchem.data.source.dataclass.SearchData
import com.google.gson.annotations.SerializedName

data class SearchDataList(
    @SerializedName("searchLogList")val searchList : ArrayList<SearchData>
)

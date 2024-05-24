package com.example.canchem.data.source.dataclass

import com.example.canchem.data.source.dataclass.FavoriteData
import com.google.gson.annotations.SerializedName

data class FavoriteDataList(
    @SerializedName("bookmarkList")val favoriteList : ArrayList<FavoriteData>
)

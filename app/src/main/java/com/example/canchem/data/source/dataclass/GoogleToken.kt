package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName

data class GoogleToken(
    @SerializedName("user_id") val userId : String?,
    @SerializedName("email") val email : String?,
    @SerializedName("name") val name : String?
//    @SerializedName("photo_url") val profileImage : String?
)

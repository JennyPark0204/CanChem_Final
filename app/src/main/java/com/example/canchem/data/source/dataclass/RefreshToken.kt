package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName

data class RefreshToken(
    @SerializedName("accessToken") val accessToken : String?,
    @SerializedName("grantType")val grantType : String?,
    @SerializedName("expiredAt") val expiredAt : String?
)

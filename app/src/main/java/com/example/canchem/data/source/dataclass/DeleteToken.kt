package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName

data class DeleteToken(
    @SerializedName("id") val id : Long?,
    @SerializedName("refreshToken")val refreshToken : String?,
    @SerializedName("accessToken")val accessToken : String?,
    @SerializedName("ttl") val expiredAt : Long?
)

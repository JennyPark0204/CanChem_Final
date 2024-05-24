package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName
import com.navercorp.nid.NaverIdLoginSDK

data class Token(
    @SerializedName("accessToken") val accessToken : String?,
    @SerializedName("refreshToken")val refreshToken : String?, //var?
    @SerializedName("grantType")val grantType : String?,
    @SerializedName("expiredAt") val expiredAt : Long?
)
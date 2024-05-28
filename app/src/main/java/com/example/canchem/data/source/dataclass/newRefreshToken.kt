package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName

data class newRefreshToken(
    @SerializedName("refreshToken")val refreshToken : String?
)

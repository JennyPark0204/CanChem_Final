package com.example.canchem.data.source.dataclass

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

data class NaverToken(
    @SerializedName("user_id") val userId : String?,
    @SerializedName("email") val email : String?,
    @SerializedName("name") val name : String?,
    @SerializedName("nickname") val nickname : String?,
    @SerializedName("mobile") val mobile : String?,
    @SerializedName("gender") val gender : String?,
    @SerializedName("profile_image") val profileImage : String?
)
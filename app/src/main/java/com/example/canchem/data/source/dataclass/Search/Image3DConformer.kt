package com.example.canchem.data.source.dataclass.Search

import com.google.gson.annotations.SerializedName

data class Image3DConformer(
    @SerializedName("bonds") val bonds: List<Int>, //결합 정보 리스트
    @SerializedName("coords") val coords: List<Double> //3D 좌표 리스트
)

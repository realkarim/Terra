package com.realkarim.home.data.model

import com.google.gson.annotations.SerializedName

data class FlagUrlsDto(
    @SerializedName("svg") val svg: String?,
    @SerializedName("png") val png: String?
)
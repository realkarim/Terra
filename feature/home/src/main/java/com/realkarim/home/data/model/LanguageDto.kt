package com.realkarim.home.data.model

import com.google.gson.annotations.SerializedName

data class LanguageDto(
    @SerializedName("name") val name: String?,
    @SerializedName("nativeName") val nativeName: String?
)
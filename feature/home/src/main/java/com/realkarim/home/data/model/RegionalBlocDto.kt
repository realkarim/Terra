package com.realkarim.home.data.model

import com.google.gson.annotations.SerializedName

data class RegionalBlocDto(
    @SerializedName("acronym") val acronym: String?,
    @SerializedName("name") val name: String?
)
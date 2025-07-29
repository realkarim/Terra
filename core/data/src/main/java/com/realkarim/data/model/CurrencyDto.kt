package com.realkarim.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyDto(
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("symbol") val symbol: String?
)
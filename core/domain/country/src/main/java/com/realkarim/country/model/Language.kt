package com.realkarim.country.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val name: String,
    val nativeName: String
)
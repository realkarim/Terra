package com.realkarim.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val name: String,
    val nativeName: String
)
package com.realkarim.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionalBloc(
    val acronym: String,
    val name: String
)
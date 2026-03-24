package com.realkarim.home.presentation

data class FilterOptions(
    val regions: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val regionalBlocs: List<String> = emptyList(),
)

package com.example.a211082_drnazatul_lab05.data

data class OnRoadUiState(
    val destination: String = "",
    val isRouting: Boolean = false,
    val savedPlaces: List<String> = listOf("Home", "Office", "UKM Bangi")
)
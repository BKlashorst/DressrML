package com.example.kledinghelper.data

data class ClothingItem(
    val id: String,
    val imagePath: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean = false,
    val type: String = "Onbekend",
    val color: String = "Onbekend",
    val season: String = "Alle"
)
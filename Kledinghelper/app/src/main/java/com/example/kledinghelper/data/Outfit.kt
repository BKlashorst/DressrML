package com.example.kledinghelper.data

data class Outfit(
    val id: String,
    val imagePath: String,
    val title: String,
    val description: String,
    val itemIds: List<String>
)
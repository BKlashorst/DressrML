package com.example.kledinghelper.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kledinghelper.data.ClothingItem
import com.example.kledinghelper.data.Outfit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class ClosetViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("ClosetPrefs", Context.MODE_PRIVATE)
    private val clothingItemsKey = "clothing_items_json"
    private val outfitsKey = "outfits_json"
    private val gson = Gson()

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _selectedType = MutableStateFlow("Alle")
    val selectedType = _selectedType.asStateFlow()

    private val _selectedColor = MutableStateFlow("Alle")
    val selectedColor = _selectedColor.asStateFlow()

    private val _selectedSeasons = MutableStateFlow<List<String>>(emptyList())
    val selectedSeasons = _selectedSeasons.asStateFlow()

    val filteredItems: StateFlow<List<ClothingItem>> = combine(
        _clothingItems, _selectedType, _selectedColor, _selectedSeasons
    ) { items, type, color, seasons ->
        items.filter { item ->
            val itemType = item.type ?: "Onbekend"
            val itemColor = item.color ?: "Onbekend"
            val itemSeason = item.season ?: "Alle"

            val typeMatch = type == "Alle" || itemType == type
            val colorMatch = color == "Alle" || itemColor == color
            val seasonMatch = seasons.isEmpty() || seasons.contains("Alle") || seasons.contains(itemSeason)

            typeMatch && colorMatch && seasonMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    val favorites: StateFlow<List<ClothingItem>> = clothingItems
        .map { list -> list.filter { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentItems: StateFlow<List<ClothingItem>> = clothingItems
        .map { it.take(4) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadItems()
        loadOutfits()
    }

    fun setTypeFilter(type: String) { _selectedType.value = type }
    fun setColorFilter(color: String) { _selectedColor.value = color }
    
    fun toggleSeasonFilter(season: String) {
        val current = _selectedSeasons.value.toMutableList()
        if (season == "Alle") {
            current.clear()
            current.add("Alle")
        } else {
            current.remove("Alle")
            if (current.contains(season)) {
                current.remove(season)
            } else {
                current.add(season)
            }
        }
        _selectedSeasons.value = current
    }

    fun saveItem(uri: Uri, title: String, description: String, type: String, color: String, season: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val inputStream = context.contentResolver.openInputStream(uri)
            val uniqueId = UUID.randomUUID().toString()
            val newFile = File(context.filesDir, "$uniqueId.jpg")

            inputStream?.use { input ->
                newFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val newItem = ClothingItem(
                id = uniqueId,
                imagePath = newFile.absolutePath,
                title = title,
                description = description,
                type = type,
                color = color,
                season = season
            )

            val currentList = _clothingItems.value.toMutableList()
            currentList.add(0, newItem)
            updateList(currentList)
        }
    }

    fun toggleFavorite(itemId: String) {
        updateItem(itemId) { it.copy(isFavorite = !it.isFavorite) }
    }

    fun updateItemDetails(itemId: String, newTitle: String, newDescription: String, newType: String, newColor: String, newSeason: String) {
        updateItem(itemId) { 
            it.copy(
                title = newTitle, 
                description = newDescription,
                type = newType,
                color = newColor,
                season = newSeason
            ) 
        }
    }

    fun deleteItem(itemId: String) {
        val currentList = _clothingItems.value.toMutableList()
        currentList.removeAll { it.id == itemId }
        updateList(currentList)
    }

    fun getItemById(itemId: String): ClothingItem? {
        return _clothingItems.value.find { it.id == itemId }
    }

    fun getOutfitById(outfitId: String): Outfit? {
        return _outfits.value.find { it.id == outfitId }
    }

    private fun updateItem(itemId: String, transformation: (ClothingItem) -> ClothingItem) {
        val currentList = _clothingItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == itemId }
        if (index != -1) {
            currentList[index] = transformation(currentList[index])
            updateList(currentList)
        }
    }

    private fun loadItems() {
        val jsonString = sharedPreferences.getString(clothingItemsKey, null)
        if (jsonString != null) {
            val type = object : TypeToken<List<ClothingItem>>() {}.type
            val items: List<ClothingItem> = gson.fromJson(jsonString, type)
            _clothingItems.value = items.map { item ->
                item.copy(
                    type = item.type ?: "Onbekend",
                    color = item.color ?: "Onbekend",
                    season = item.season ?: "Alle"
                )
            }
        }
    }

    private fun updateList(list: List<ClothingItem>) {
        _clothingItems.value = list
        val jsonString = gson.toJson(list)
        sharedPreferences.edit().putString(clothingItemsKey, jsonString).apply()
    }

    private fun loadOutfits() {
        val jsonString = sharedPreferences.getString(outfitsKey, null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Outfit>>() {}.type
            _outfits.value = gson.fromJson(jsonString, type)
        } else {
            val currentItems = _clothingItems.value
            if (currentItems.size >= 2) {
                val dummyOutfits = listOf(
                    Outfit(
                        id = "outfit1",
                        imagePath = currentItems[0].imagePath,
                        title = "Zomerse Look",
                        description = "Perfect voor een warme dag.",
                        itemIds = listOf(currentItems[0].id)
                    ),
                    Outfit(
                        id = "outfit2",
                        imagePath = currentItems[1].imagePath,
                        title = "Casual Outfit",
                        description = "Comfortabel en stijlvol.",
                        itemIds = listOf(currentItems[1].id)
                    )
                )
                _outfits.value = dummyOutfits
                saveOutfits(dummyOutfits)
            }
        }
    }

    private fun saveOutfits(list: List<Outfit>) {
        val jsonString = gson.toJson(list)
        sharedPreferences.edit().putString(outfitsKey, jsonString).apply()
    }
}
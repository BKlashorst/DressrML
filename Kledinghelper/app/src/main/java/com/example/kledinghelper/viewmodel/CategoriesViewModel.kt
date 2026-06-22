package com.example.kledinghelper.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("CategoriesPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _types = MutableStateFlow(loadList("types",
        listOf("T-shirt", "Overhemd", "Colbert", "Gilet", "Broek", "Trui", "Jas", "Schoenen")))
    val types: StateFlow<List<String>> = _types.asStateFlow()

    private val _dresscodes = MutableStateFlow(loadList("dresscodes",
        listOf("Formeel", "Casual", "School", "Bruiloft")))
    val dresscodes: StateFlow<List<String>> = _dresscodes.asStateFlow()

    private fun loadList(key: String, default: List<String>): List<String> {
        val json = prefs.getString(key, null) ?: return default
        return try { gson.fromJson(json, object : TypeToken<List<String>>() {}.type) } catch (e: Exception) { default }
    }

    private fun saveList(key: String, list: List<String>) {
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }

    fun addType(name: String) { if (name.isNotBlank() && !_types.value.contains(name)) { _types.value = _types.value + name; saveList("types", _types.value) } }
    fun removeType(index: Int) { _types.value = _types.value.toMutableList().also { it.removeAt(index) }; saveList("types", _types.value) }
    fun updateType(index: Int, name: String) { _types.value = _types.value.toMutableList().also { it[index] = name }; saveList("types", _types.value) }

    fun addDresscode(name: String) { if (name.isNotBlank() && !_dresscodes.value.contains(name)) { _dresscodes.value = _dresscodes.value + name; saveList("dresscodes", _dresscodes.value) } }
    fun removeDresscode(index: Int) { _dresscodes.value = _dresscodes.value.toMutableList().also { it.removeAt(index) }; saveList("dresscodes", _dresscodes.value) }
    fun updateDresscode(index: Int, name: String) { _dresscodes.value = _dresscodes.value.toMutableList().also { it[index] = name }; saveList("dresscodes", _dresscodes.value) }
}

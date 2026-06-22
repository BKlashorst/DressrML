package com.example.kledinghelper.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kledinghelper.api.WeatherApi
import com.example.kledinghelper.data.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data class Success(val weather: WeatherResponse) : WeatherUiState
    data class Error(val message: String?) : WeatherUiState
    object Loading : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)
        private set

    private val prefs = application.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _city = MutableStateFlow(prefs.getString("saved_city", "Eindhoven") ?: "Eindhoven")
    val city: StateFlow<String> = _city.asStateFlow()

    init {
        // Toon direct gecachede data zodat het scherm niet leeg blijft
        loadCachedWeather()
        // Dan refresh op de achtergrond
        fetchWeather(_city.value)
    }

    fun updateCity(newCity: String) {
        if (newCity.isNotBlank()) fetchWeather(newCity)
    }

    private fun loadCachedWeather() {
        val cached = prefs.getString("cached_weather", null) ?: return
        try {
            val weather = gson.fromJson(cached, WeatherResponse::class.java)
            weatherUiState = WeatherUiState.Success(weather)
        } catch (_: Exception) {}
    }

    private fun fetchWeather(city: String) {
        viewModelScope.launch {
            // Alleen loading tonen als er nog geen data is
            if (weatherUiState !is WeatherUiState.Success) {
                weatherUiState = WeatherUiState.Loading
            }
            try {
                val result = WeatherApi.retrofitService.getCurrentWeather(city)
                weatherUiState = WeatherUiState.Success(result)
                _city.value = result.name
                prefs.edit()
                    .putString("saved_city", result.name)
                    .putString("cached_weather", gson.toJson(result))
                    .apply()
            } catch (e: Exception) {
                // Bij fout: cached data blijft zichtbaar, geen laadindicator
                if (weatherUiState !is WeatherUiState.Success) {
                    weatherUiState = WeatherUiState.Error(e.message)
                }
                _city.value = city
            }
        }
    }
}

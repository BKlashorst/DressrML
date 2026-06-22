package com.example.kledinghelper.api

import com.example.kledinghelper.data.WeatherResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val API_KEY  = "5298ab5016cddd01fc500d4b059de6fb"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(8, TimeUnit.SECONDS)
    .writeTimeout(8, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q")     city: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String  = "metric",
        @Query("lang")  lang: String   = "nl"
    ): WeatherResponse
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}

package com.ryall.weatherapp.api

import com.ryall.weatherapp.models.WeatherResponse
import com.ryall.weatherapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    suspend fun getCityWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherResponse>
}
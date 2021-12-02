package com.ryall.weatherapp.repository

import com.ryall.weatherapp.api.ApiService
import com.ryall.weatherapp.util.Constants.Companion.API_KEY

class WeatherRepository(private val apiService: ApiService){
    suspend fun getWeather(lat:Double, Lon:Double) = apiService.getCityWeather( lat,Lon, API_KEY)
}

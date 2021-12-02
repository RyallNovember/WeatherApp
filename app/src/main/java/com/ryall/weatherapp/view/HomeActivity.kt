package com.ryall.weatherapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ryall.weatherapp.R
import com.ryall.weatherapp.databinding.ActivityHomeBinding
import com.ryall.weatherapp.util.Constants.Companion.ICON_URL
import com.ryall.weatherapp.util.NetworkConnection
import com.ryall.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), LocationListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel: WeatherViewModel by viewModels()

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkInternetConnection()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnRefresh.setOnClickListener {
            checkInternetConnection()
        }
    }

    private fun checkInternetConnection(){
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this,  { isConnected->
            if (isConnected){
                fetchLocation()
            }else{
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }
        binding.progressbar.visibility = View.VISIBLE
        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                getWeather(latitude, longitude)
            }
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        viewModel.getWeather(lat, lon)
        viewModel.weatherResponse.observe(this, { weather ->
            var description = ""
            weather.weather.forEach {
                description = it.description
            }
            val temperature = convertTemp(weather.main.temp)
            binding.tvTemperature.text = temperature.toString()
            binding.tvSymbol.visibility = View.VISIBLE
            binding.tvWeatherDescription.text = description
            binding.tvCity.text = weather.name
            binding.tvCountry.text = weather.sys.country
            binding.progressbar.visibility = View.GONE
            val icon = weather.weather[0].icon
            val url = "$ICON_URL$icon@4x.png"
            Glide.with(binding.ivWeatherIcon)
                .load(url)
                .into(binding.ivWeatherIcon)
        })
    }

    private fun convertTemp(kelvin: Double): Int {
        val temperature = kelvin - 273.15
        return temperature.roundToInt()
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
    }
}
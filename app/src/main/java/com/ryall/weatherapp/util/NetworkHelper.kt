package com.ryall.weatherapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object NetworkHelper {

    fun isNetworkConnected(context: Context): Boolean {
        var result: Boolean
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            result = checkNetworkConnection(this, this.activeNetwork)
        }
        return result
    }

    private fun checkNetworkConnection(
        connectivityManager: ConnectivityManager,
        network: Network?
    ): Boolean {
        connectivityManager.getNetworkCapabilities(network)?.apply {
            if (this.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (this.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
        return false
    }
}
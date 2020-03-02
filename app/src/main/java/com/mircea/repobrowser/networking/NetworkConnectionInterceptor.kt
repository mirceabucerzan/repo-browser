package com.mircea.repobrowser.networking

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * [Interceptor] which checks Internet connectivity and throws [NoNetworkException] if necessary.
 */
class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) throw NoNetworkException()
        return chain.proceed(chain.request())
    }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        return (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager)?.let { it.activeNetworkInfo?.isConnected } == true
    }

}

/**
 * Exception thrown when there's not Internet connection while attempting server requests.
 */
class NoNetworkException : IOException("No network connection")
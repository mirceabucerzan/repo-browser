package com.mircea.repobrowser.networking

import android.content.Context
import com.mircea.repobrowser.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Singleton manager for the OkHttpClient instance used for API calls.
 */
object HttpClientManager {
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    private var client: OkHttpClient? = null
    val httpClient: OkHttpClient
        get() = client ?: throw IllegalStateException("HttpClientManager not initialized")

    @Synchronized
    fun init(context: Context) {
        Timber.d("Initializing")
        if (client == null) {
            client = OkHttpClient.Builder().run {
                connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                addInterceptor(NetworkConnectionInterceptor(context))
                addInterceptor(ApiVersionInterceptor())
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().also {
                        it.level = HttpLoggingInterceptor.Level.BODY
                    })
                }
                build()
            }
        }
    }
}
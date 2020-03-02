package com.mircea.repobrowser.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PROTOCOL = "https"
private const val HOSTNAME = "api.github.com"
private const val BASE_URL = "$PROTOCOL://$HOSTNAME"

/**
 * Returns an implementation of the API endpoints defined by the [serviceClass] interface.
 */
fun <T> provideRetrofitApi(serviceClass: Class<T>): T {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(HttpClientManager.httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(serviceClass)
}
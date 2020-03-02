package com.mircea.repobrowser.networking

import okhttp3.Interceptor
import okhttp3.Response

/**
 * [Interceptor] which adds the correct GitHub API version header,
 * as per https://developer.github.com/v3/#current-version
 */
class ApiVersionInterceptor : Interceptor {
    companion object {
        private const val HEADER_ACCEPT = "Accept"
        private const val MEDIA_TYPE_GITHUB = "application/vnd.github.v3+json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request()
                .newBuilder()
                .header(HEADER_ACCEPT, MEDIA_TYPE_GITHUB)
                .build()
        )
    }
}
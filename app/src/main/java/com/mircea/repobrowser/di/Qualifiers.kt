package com.mircea.repobrowser.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NetworkConnectionInterceptorQ

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiVersionInterceptorQ

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class HttpLoggingInterceptorQ
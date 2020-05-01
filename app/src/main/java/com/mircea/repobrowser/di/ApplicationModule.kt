package com.mircea.repobrowser.di

import com.mircea.repobrowser.BuildConfig
import com.mircea.repobrowser.data.DefaultGitHubRepository
import com.mircea.repobrowser.data.GitHubApi
import com.mircea.repobrowser.data.GitHubRepository
import com.mircea.repobrowser.networking.ApiVersionInterceptor
import com.mircea.repobrowser.networking.NetworkConnectionInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * [Module] that lists the objects provided by Dagger which are tied to the application's scope.
 */
@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {
    private const val PROTOCOL = "https"
    private const val HOSTNAME = "api.github.com"
    private const val BASE_URL = "$PROTOCOL://$HOSTNAME"
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    @JvmStatic
    @Singleton
    @Provides
    fun provideGitHubApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory
    ): GitHubApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(GitHubApi::class.java)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideOkHttpClient(
        @NetworkConnectionInterceptorQ networkConnectionInterceptor: Interceptor,
        @ApiVersionInterceptorQ apiVersionInterceptor: Interceptor,
        @HttpLoggingInterceptorQ httpLoggingInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().run {
            connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            addInterceptor(networkConnectionInterceptor)
            addInterceptor(apiVersionInterceptor)
            if (BuildConfig.DEBUG) {
                addInterceptor(httpLoggingInterceptor)
            }
            build()
        }
    }

    @JvmStatic
    @Provides
    fun provideGsonConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @JvmStatic
    @HttpLoggingInterceptorQ
    @Provides
    fun provideHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
    }
}

@Module
abstract class ApplicationModuleBinds {

    @Singleton
    @Binds
    abstract fun bindGitHubRepository(repository: DefaultGitHubRepository): GitHubRepository

    @NetworkConnectionInterceptorQ
    @Binds
    abstract fun bindNetworkConnectionInterceptor(
        interceptor: NetworkConnectionInterceptor
    ): Interceptor

    @ApiVersionInterceptorQ
    @Binds
    abstract fun bindApiVersionInterceptor(interceptor: ApiVersionInterceptor): Interceptor
}
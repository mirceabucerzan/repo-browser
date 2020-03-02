package com.mircea.repobrowser.networking

import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Triggers the API [call] and maps the [Response] to an appropriate [Result].
 */
suspend fun <T : Any> callApi(call: suspend () -> Response<T>): Result<T> {
    try {
        val response = call.invoke()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Result.Success(body)
        } else {
            Timber.e("${call.javaClass.simpleName} -> server error response code=[${response.code()}] message=[${response.message()}]")
            Result.Error(IOException())
        }
    } catch (e: HttpException) {
        Timber.e("${call.javaClass.simpleName} -> server error response code=[${e.code()}] message=[${e.message()}]")
        return Result.Error(e)
    } catch (e: NoNetworkException) {
        Timber.e("No network connection")
        return Result.Error(e)
    } catch (e: Throwable) {
        Timber.e("${call.javaClass.simpleName} -> error message=[${e.message}]")
        return Result.Error(IOException())
    }
}
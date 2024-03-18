package com.example.cloudy


sealed class ApiState<out T> {
    data class Success<T>(val data: T) : ApiState<List<T>>()
    data class Failure<T>(val message: Throwable) : ApiState<T>()
    object Loading : ApiState<Nothing>()
}
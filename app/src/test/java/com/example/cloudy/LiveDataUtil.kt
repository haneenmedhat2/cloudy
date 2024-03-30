package com.example.cloudy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.launch



@ExperimentalCoroutinesApi
suspend fun <T> Flow<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterCollect: suspend () -> Unit = {}
): T {
    val values = Channel<T>(Channel.CONFLATED)
  /*  val job = launch {
        collect {
            values.trySend(it).isSuccess
        }
    }*/
    val value = withTimeout(timeUnit.toMillis(time)) {
        afterCollect()
        values.receive()
    }
    //job.cancel()
    return value
}
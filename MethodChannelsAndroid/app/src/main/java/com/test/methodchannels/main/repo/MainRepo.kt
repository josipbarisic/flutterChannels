package com.test.methodchannels.main.repo

import com.test.methodchannels.main.data.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class MainRepo {
    fun fetchRealtimeWeather() = flow {
        emit(Result.Loading)
        while (true) {
            delay(1000)
            val currentTemp = (10..35).random()
            if ((10..13).contains(currentTemp))
                emit(
                    Result.Error(
                        Exception("Temperature too low.")
                    )
                )
            else emit(Result.Success(currentTemp))
        }
    }
}
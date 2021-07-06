package com.test.methodchannels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.test.methodchannels.main.data.Result
import com.test.methodchannels.main.repo.MainRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class MainViewModel(mainRepo: MainRepo) : ViewModel() {

    private val _realtimeWeatherFlow = mainRepo.fetchRealtimeWeather()
    private val _realtimeWeather = _realtimeWeatherFlow
        .map { temp ->
            when (temp) {
                is Result.Loading -> "Loading..."
                is Result.Success -> "${temp.data}"
                is Result.Error -> "Error: ${temp.exception.message}"
            }
        }
        .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    val realtimeWeather: LiveData<String>
        get() = _realtimeWeather
}
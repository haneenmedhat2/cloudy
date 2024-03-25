package com.example.cloudy.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.AlertResponse
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.model.WeatherResponse
import com.example.cloudy.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repositoryImp: WeatherRepositoryImp): ViewModel() {

    private var _alert = MutableStateFlow<List<Alert?>>(emptyList())
    var alert= _alert.asStateFlow()

    private var _alertData = MutableStateFlow<List<AlertData?>>(emptyList())
    var alertData= _alertData.asStateFlow()

    private var _alertList = MutableStateFlow<ApiState<List<AlertResponse?>>>(ApiState.Loading)
    var alertList= _alertList.asStateFlow()
    init {
        getAllAlert()
        getAlertData()
    }

    //Retrofit
    fun getWeatherAlert(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImp.getWeatherAlert(latitude, longitude, apiKey)
                .catch { e ->
                    _alertList.value= ApiState.Failure(e)
                }
                .collect { data -> _alertList.value= ApiState.Success(data) }
        }
    }


    //Alert Data
    fun inserAlertData(alert: AlertData) {
        viewModelScope.launch {
            repositoryImp.insertAlertData(alert)

        }
    }

    fun getAlertData(){
        viewModelScope.launch{
            repositoryImp.getAlertData().collect { alerts ->
                _alertData.value = alerts
            }
        }
    }

    fun deleteAlertData(alert: AlertData){
         viewModelScope.launch {
             repositoryImp.deleteAlertData(alert)
             getAlertData()
         }

    }

       //Alert
    fun inserAlert(alert: Alert) {
        viewModelScope.launch {
            repositoryImp.insertAlert(alert)

        }
    }


     fun deleteAlert(alert: Alert) {
          viewModelScope.launch {
              repositoryImp.deleteAlert(alert)
              getAllAlert()

          }
      }

    fun getAllAlert(){
        viewModelScope.launch{
            repositoryImp.getAllAlerts().collect { alerts ->
                _alert.value = alerts
            }
        }
    }
}
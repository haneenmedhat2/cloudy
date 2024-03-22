package com.example.cloudy.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel (private val repositoryImp: WeatherRepositoryImp): ViewModel() {

    private var _alert = MutableStateFlow<List<Alert?>>(emptyList())
    var alert= _alert.asStateFlow()

    private var _alertData = MutableStateFlow<List<AlertData?>>(emptyList())
    var alertData= _alertData.asStateFlow()
    init {
        getAllAlert()
    }

    //Alert Data
    fun inserAlertData(alert: AlertData) {
        viewModelScope.launch {
            repositoryImp.insertAlertData(alert)

        }
    }
       //Alert
    fun inserAlert(alert: Alert) {
        viewModelScope.launch {
            repositoryImp.insertAlert(alert)

        }
    }


    /*  fun deleteCity(cityName: MapCity) {
          viewModelScope.launch {
              repositoryImp.deleteCity(cityName)
              getAllCity()

          }
      }*/

    fun getAllAlert(){
        viewModelScope.launch{
            repositoryImp.getAllAlerts().collect { alerts ->
                _alert.value = alerts
            }
        }
    }
}
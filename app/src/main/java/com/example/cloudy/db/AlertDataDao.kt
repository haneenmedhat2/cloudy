package com.example.cloudy.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDataDao {

   @Query ("SELECT * FROM alert_data")
    fun getAlertData():Flow<List<AlertData>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlertData(alert: AlertData):Long


    @Delete
    suspend fun deleteAlertData(alert: AlertData):Int
}
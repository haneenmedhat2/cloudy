package com.example.cloudy.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloudy.model.Alert
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

   @Query ("SELECT * FROM alert")
    fun getAllAlerts():Flow<List<Alert>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlert(alert: Alert):Long


    @Delete
    suspend fun deleteAlert(alert: Alert):Int
}
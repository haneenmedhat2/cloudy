package com.example.cloudy.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

   @Query ("SELECT * FROM city")
    fun getAllCities():Flow<List<MapCity>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCity(city: MapCity):Long


    @Delete
    suspend fun deleteCity(city: MapCity):Int
}
package com.example.cloudy.db

import android.content.Context
import android.location.Address
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloudy.model.MapCity

@Database (entities = arrayOf( MapCity::class), version = 1)
abstract class CityDatabase:RoomDatabase() {
    abstract fun getAllCities(): CityDao
    companion object{
        @Volatile
        private var INSTANCE: CityDatabase?=null

        fun getInstance(context: Context): CityDatabase {
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext, CityDatabase::class.java,"city_database")
                    .build()
                INSTANCE =instance
                instance
            }
        }
    }

}
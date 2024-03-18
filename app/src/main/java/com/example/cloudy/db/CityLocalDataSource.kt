package com.example.cloudy.db

import com.example.cloudy.model.MapCity

interface CityLocalDataSource {
    suspend fun insertCity(city: MapCity)

/*    suspend fun deleteProduct(product: Product)
     fun getAllProducts():Flow<List<Product>>*/
}
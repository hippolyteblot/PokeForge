package com.example.pokeforge

import com.example.pokeforge.pojo.*
import retrofit2.http.*
import retrofit2.http.GET


interface APINameInterface {
    // Same using @Query
    @GET("/fusion")
    suspend fun doGetFusionName(@Query("ids") ids: String): PokemonAPIName?


}
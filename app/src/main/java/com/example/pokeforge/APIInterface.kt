package com.example.pokeforge

import com.example.pokeforge.pojo.PokemonAPI
import com.example.pokeforge.pojo.PokemonAPIType
import com.example.pokeforge.pojo.PokemonAPIInfos
import retrofit2.http.*
import retrofit2.http.GET


interface APIInterface {
    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListResources(@Path("id") int: Int): PokemonAPI?

    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListType(@Path("id") int: Int): PokemonAPIType?

    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListInfos(@Path("id") int: Int): PokemonAPIInfos?

}
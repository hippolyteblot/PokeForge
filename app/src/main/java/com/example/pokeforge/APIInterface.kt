package com.example.pokeforge

import com.example.pokeforge.pojo.*
import retrofit2.http.*
import retrofit2.http.GET


interface APIInterface {
    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListResources(@Path("id") int: Int): PokemonAPI?

    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListType(@Path("id") int: Int): PokemonAPIType?

    @GET("/api/v2/pokemon/{id}")
    suspend fun doGetListInfos(@Path("id") int: Int): PokemonAPIInfos?

    @GET("/api/v2/pokemon-species/{id}")
    suspend fun doGetEvolutionLink(@Path("id") int: Int): PokemonAPIEvolutionId?

    @GET("/api/v2/evolution-chain/{id}")
    suspend fun doGetEvolutionChain(@Path("id") int: Int): PokemonAPIEvolutionChain?

}
package com.example.pokeforge

import com.example.pokeforge.pojo.MultipleResource
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {
    @GET("/api/v2/pokemon/{id}")
    fun doGetListResources(@Path("id") int: Int): Call<MultipleResource?>?




}
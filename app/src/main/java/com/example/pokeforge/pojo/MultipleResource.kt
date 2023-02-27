package com.example.pokeforge.pojo


import com.google.gson.annotations.SerializedName


data class MultipleResource (
    var results: List<Pokemon>
)

data class Pokemon(
    val name: String,
    val url: String
)
package com.example.pokeforge.pojo


import com.google.gson.annotations.SerializedName
import java.util.Objects


data class MultipleResource (
    var species: Objects
)

data class Species (
    val name: String,
    val url: String
)
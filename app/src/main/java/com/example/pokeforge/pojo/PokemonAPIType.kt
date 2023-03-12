package com.example.pokeforge.pojo

import com.google.gson.annotations.SerializedName

class PokemonAPIType {
    @SerializedName("types")
    var types: List<Datum> = ArrayList()

    inner class Datum {

        @SerializedName("type")
        var type: Type? = null
    }

    data class Type(
        @SerializedName("name")
        val name: String?,

    )
}


package com.example.pokeforge.pojo


import com.google.gson.annotations.SerializedName


class PokemonAPI {

    @SerializedName("stats")
    var stats: List<Datum> = ArrayList()

    inner class Datum {

        @SerializedName("base_stat")
        var baseStat: Int? = null
    }


}
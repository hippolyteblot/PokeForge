package com.example.pokeforge.pojo

import com.google.gson.annotations.SerializedName

class PokemonAPIEvolutionId {

    @SerializedName("evolution_chain")
    var evolutionChain: EvolutionChain? = null

    inner class EvolutionChain {
        @SerializedName("url")
        var url: String? = null
    }

}
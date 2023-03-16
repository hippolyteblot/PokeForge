package com.example.pokeforge.pojo

import com.google.gson.annotations.SerializedName

class PokemonAPIEvolutionChain {

    @SerializedName("chain")
    var chain: Chain? = null

    inner class Chain {
        @SerializedName("species")
        var species: Species? = null

        @SerializedName("evolves_to")
        var evolvesTo: List<EvolvesTo> = ArrayList()

        inner class Species {
            @SerializedName("name")
            var name: String? = null

            @SerializedName("url")
            var url: String? = null
        }

        inner class EvolvesTo {
            @SerializedName("species")
            var species: Species? = null

            @SerializedName("evolves_to")
            var evolvesTo: List<EvolvesTo> = ArrayList()

            inner class Species {
                @SerializedName("name")
                var name: String? = null

                @SerializedName("url")
                var url: String? = null
            }
        }
    }

}
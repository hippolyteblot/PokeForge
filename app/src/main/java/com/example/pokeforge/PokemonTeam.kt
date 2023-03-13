package com.example.pokeforge

object PokemonTeam {

    private var team = ArrayList<Pokemon>()

    fun addPokemon(pokemon: Pokemon) {
        team.add(pokemon)
    }

    fun removePokemon(pokemon: Pokemon) {
        team.remove(pokemon)
    }

    fun getTeam(): ArrayList<Pokemon> {
        return team
    }

    fun getTeamWthoutEgg(): ArrayList<Pokemon> {
        val teamWithoutEgg = ArrayList<Pokemon>()
        for (pokemon in team) {
            if (pokemon.name != "Egg") {
                teamWithoutEgg.add(pokemon)
            }
        }
        return teamWithoutEgg
    }

    fun getTeamSize(): Int {
        return team.size
    }

    fun getTeamIncome(): Int {
        var income = 0
        for (pokemon in team) {
            income += pokemon.income
        }
        return income
    }

    fun setTeam(team: ArrayList<Pokemon>) {
        this.team = team
    }

    fun openEgg(pokemon: Pokemon) {
        team.remove(pokemon)
        team.add(pokemon)
    }
}
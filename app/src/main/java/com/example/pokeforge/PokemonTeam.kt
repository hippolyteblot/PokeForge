package com.example.pokeforge
@Suppress("unused")
object PokemonTeam {

    private var team = ArrayList<Pokemon>()

    fun addPokemon(pokemon: Pokemon) {
        team.add(pokemon)
    }

    fun removePokemon(pokemon: Pokemon) {
        team.remove(pokemon)
    }

    fun getTeam(): ArrayList<Pokemon> {
        val sortedTeam = team.sortBy { it.name }
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

    fun updatePokemon(pokemon: Pokemon) {
        val id = pokemon.id
        for (i in 0 until team.size) {
            if (team[i].id == id) {
                team[i] = pokemon
            }
        }
    }
}
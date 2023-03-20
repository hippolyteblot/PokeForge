package com.example.pokeforge

data class Pokemon(
    var name: String,
    val id: String,
    var types: MutableList<PokemonType>,
    var height: Int,
    var weight: Int,
    val exp: Int,
    var stats: List<Int>,
    var income: Int,
    var dna: List<Int> = listOf(0, 0),
    var isEgg: Boolean = false,
) : java.io.Serializable
enum class PokemonType {
    NORMAL,
    FIGHTING,
    FLYING,
    POISON,
    GROUND,
    ROCK,
    BUG,
    GHOST,
    STEEL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    PSYCHIC,
    ICE,
    DRAGON,
    DARK,
    FAIRY,
    UNKNOWN,
    SHADOW
}
package com.example.pokeforge

data class Pokemon (
    val name: String,
    val id: Int,
    val types: List<PokemonType>,
    val height: Int,
    val weight: Int,
    val exp: Int,
    val stats: List<Int>,
    val income: Int,
    val dna: List<Int> = listOf(0, 0)
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
package com.example.pokeforge

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pokeforge.databinding.ActivityPokemonViewerBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class PokemonViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPokemonViewerBinding
    private lateinit var pokemon: Pokemon

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pokemon = intent.getSerializableExtra("pokemon") as Pokemon

        if(pokemon.isEgg) {
            openEgg()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }


        Log.d("poke", pokemon.toString())
        GlobalScope.launch {
            pokemon.stats = getStatsOf(pokemon.dna[0], pokemon.dna[1])
            pokemon.types = getTypeOf(pokemon)
            pokemon.weight = getWeightOf(pokemon)
            pokemon.height = getHeightOf(pokemon)
            //pokemon.name = getNameOf(pokemon)
            runOnUiThread {
                bind(pokemon)
                binding.statPvNb.text = pokemon.stats[0].toString()
                binding.statPvDef.text = pokemon.stats[1].toString()
                binding.statPvDefSpe.text = pokemon.stats[2].toString()
                binding.statPvAttack.text = pokemon.stats[3].toString()
                binding.statPvAttackSpe.text = pokemon.stats[4].toString()
                binding.statPvSpeed.text = pokemon.stats[5].toString()
                binding.pokemonName.text = pokemon.name
                binding.pokemonHeight.text = pokemon.height.toString() + "0 cm"
                var res = pokemon.weight.toString()
                if (res.length > 1) {
                    res = res.substring(0, res.length - 1) + "." + res.substring(res.length - 1)
                }
                binding.pokemonWeight.text = "$res kg"
                Log.d("poke", pokemon.toString())

            }






        }
        //bind(pokemon)

    }

    private fun bind(pokemon: Pokemon) {
        // Bind info
        binding.pokemonName.text = pokemon.name
        // Bind types
        val type1 = pokemon.types[0]
        val bitmap1 = BitmapFactory.decodeResource(resources, getTypeSprite(type1))
        val resizedBitmap1 = Bitmap.createScaledBitmap(bitmap1, 180, 180, false)
        binding.pokemonTypeSprite1.setImageBitmap(resizedBitmap1)
        if (pokemon.types.size > 1) {
            val type2 = pokemon.types[1]
            // Rescale the second type sprite
            val bitmap2 = BitmapFactory.decodeResource(resources, getTypeSprite(type2))
            val resizedBitmap2 = Bitmap.createScaledBitmap(bitmap2, 180, 180, false)
            binding.pokemonTypeSprite2.setImageBitmap(resizedBitmap2)
        } else {
            // Delete the second type sprite
            binding.pokemonTypeSprite2.setImageResource(0)
        }

        // Bind sprite
        APISpritesClient.setSpriteImage(pokemon.dna, binding.pokemonSprite, this)


    }

    private fun getTypeSprite(type: PokemonType) : Int {
        return when (type) {
            PokemonType.NORMAL -> R.drawable.type_normal
            PokemonType.FIRE -> R.drawable.type_fire
            PokemonType.WATER -> R.drawable.type_water
            PokemonType.ELECTRIC -> R.drawable.type_electric
            PokemonType.GRASS -> R.drawable.type_grass
            PokemonType.ICE -> R.drawable.type_ice
            PokemonType.FIGHTING -> R.drawable.type_fighting
            PokemonType.POISON -> R.drawable.type_poison
            PokemonType.GROUND -> R.drawable.type_ground
            PokemonType.FLYING -> R.drawable.type_flying
            PokemonType.PSYCHIC -> R.drawable.type_psychic
            PokemonType.BUG -> R.drawable.type_bug
            PokemonType.ROCK -> R.drawable.type_rock
            PokemonType.GHOST -> R.drawable.type_ghost
            PokemonType.DRAGON -> R.drawable.type_dragon
            PokemonType.DARK -> R.drawable.type_dark
            PokemonType.STEEL -> R.drawable.type_steel
            PokemonType.FAIRY -> R.drawable.type_fairy
            else -> {
                R.drawable.type_normal
            }
        }
    }



    private suspend fun getStatById(id: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        val pokemonRes = APIClient.apiService
        val result = try {
            pokemonRes.doGetListResources(id)?.stats
        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
            null
        }
        for (i in result?.indices!!) {
            Log.d("TAG", "getStatsOf: ${result[i].baseStat}")
            result[i].baseStat?.let { list.add(it) }
        }
        return list
    }

    private suspend fun getStatsOf(dna1:Int, dna2:Int) : ArrayList<Int> {
        val id1 = dna1
        var id2 = dna2
        if (dna2 == 0) {
            id2 = id1
        }
        val pokemonStat1 = getStatById(id1)
        val pokemonStat2 = getStatById(id2)
        val intPokemonStat1 = ArrayList<Int>()
        val intPokemonStat2 = ArrayList<Int>()
        val finalPokemonStat = ArrayList<Int>()
        pokemonStat1.forEach { stat ->
            intPokemonStat1.add(stat)
        }
        pokemonStat2.forEach { stat ->
            intPokemonStat2.add(stat)
        }
        for (i in intPokemonStat1.indices) {
            finalPokemonStat.add((intPokemonStat1[i] + intPokemonStat2[i]) / 2)
            Log.d("TAG", "getStatsOf: ${finalPokemonStat[i]} ${intPokemonStat1[i]} ${intPokemonStat2[i]}")
        }



        return finalPokemonStat
    }

    private suspend fun getTypeOf(pokemon: Pokemon): MutableList<PokemonType> {
        val types : MutableList<PokemonType> = mutableListOf()
        val pokemonRes = APIClient.apiService
        try {

            val res = pokemonRes.doGetListType(pokemon.dna[0])?.types
            var id2 = pokemon.dna[1]
            if(pokemon.dna[1] == 0) {
                id2 = pokemon.dna[0]
            }
            val res2 = pokemonRes.doGetListType(id2)?.types
            if (res2 != null) {
                    val resType = res2[0].type
                when(resType?.name){
                        "normal" -> types.add(PokemonType.NORMAL)
                        "fire" -> types.add(PokemonType.FIRE)
                        "water" -> types.add(PokemonType.WATER)
                        "electric" -> types.add(PokemonType.ELECTRIC)
                        "grass" -> types.add(PokemonType.GRASS)
                        "ice" -> types.add(PokemonType.ICE)
                        "fighting" -> types.add(PokemonType.FIGHTING)
                        "poison" -> types.add(PokemonType.POISON)
                        "ground" -> types.add(PokemonType.GROUND)
                        "flying" -> types.add(PokemonType.FLYING)
                        "psychic" -> types.add(PokemonType.PSYCHIC)
                        "bug" -> types.add(PokemonType.BUG)
                        "rock" -> types.add(PokemonType.ROCK)
                        "ghost" -> types.add(PokemonType.GHOST)
                        "dragon" -> types.add(PokemonType.DRAGON)
                        "dark" -> types.add(PokemonType.DARK)
                        "steel" -> types.add(PokemonType.STEEL)
                        "fairy" -> types.add(PokemonType.FAIRY)
                        else -> {
                            types.add(PokemonType.UNKNOWN)
                        }

                }
            } else {
                types.add(PokemonType.UNKNOWN)
            }
            if (res != null) {
                    val resType = res[0].type
                when(resType?.name){
                        "normal" -> types.add(PokemonType.NORMAL)
                        "fire" -> types.add(PokemonType.FIRE)
                        "water" -> types.add(PokemonType.WATER)
                        "electric" -> types.add(PokemonType.ELECTRIC)
                        "grass" -> types.add(PokemonType.GRASS)
                        "ice" -> types.add(PokemonType.ICE)
                        "fighting" -> types.add(PokemonType.FIGHTING)
                        "poison" -> types.add(PokemonType.POISON)
                        "ground" -> types.add(PokemonType.GROUND)
                        "flying" -> types.add(PokemonType.FLYING)
                        "psychic" -> types.add(PokemonType.PSYCHIC)
                        "bug" -> types.add(PokemonType.BUG)
                        "rock" -> types.add(PokemonType.ROCK)
                        "ghost" -> types.add(PokemonType.GHOST)
                        "dragon" -> types.add(PokemonType.DRAGON)
                        "dark" -> types.add(PokemonType.DARK)
                        "steel" -> types.add(PokemonType.STEEL)
                        "fairy" -> types.add(PokemonType.FAIRY)
                        else -> {
                            types.add(PokemonType.UNKNOWN)
                        }

                }
            } else {
                types.add(PokemonType.UNKNOWN)
            }

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")

        }
        //Log.d("TAG", "getTypeOf: ${types[0]} ${types[1]}")
        return types



    }
    private suspend fun getWeightOf(pokemon: Pokemon): Int {
        var weight : String? = null
        val pokemonRes = APIClient.apiService
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            weight = if (pokemonRes.doGetListInfos(pokemon.dna[0]) != null) {
                pokemonRes.doGetListInfos(pokemon.dna[0])?.weight
            } else {
                pokemonRes.doGetListInfos(pokemon.dna[1])?.weight
            }

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return weight?.toInt() ?: 0
    }

    private suspend fun getHeightOf(pokemon: Pokemon): Int {
        var height : String? = null
        val pokemonRes = APIClient.apiService
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            height = if (pokemonRes.doGetListInfos(pokemon.dna[0]) != null) {
                pokemonRes.doGetListInfos(pokemon.dna[0])?.height
            } else {
                pokemonRes.doGetListInfos(pokemon.dna[1])?.height
            }

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return height?.toInt() ?: 0
    }

    private suspend fun getNameOf(pokemon: Pokemon): String {
        val pokemonRes = APIClient.apiService
        var name : String? = null
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            val name0 = pokemonRes.doGetListInfos(pokemon.dna[0])?.name
            //name0 first char to upper case and the rest to lower case
            val name0UpperCase = pokemonRes.doGetListInfos(pokemon.dna[0])?.name?.substring(0,1)
                ?.uppercase(
                    Locale.ROOT
                ) + pokemonRes.doGetListInfos(pokemon.dna[0])?.name?.substring(1)
                ?.lowercase(Locale.ROOT)
            name = name0UpperCase + "/" + pokemonRes.doGetListInfos(pokemon.dna[1])?.name?.substring(0,1)
                ?.uppercase(Locale.ROOT) + pokemonRes.doGetListInfos(pokemon.dna[1])?.name?.substring(1)
                ?.lowercase(Locale.ROOT)
            //

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return name ?: "Unknown"
    }

    fun openEgg() {
        val db = Firebase.firestore
        // Update the pokemon with id to egg = false
        db.collection("pokemons").document(pokemon.id).update("egg", false)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        pokemon.isEgg = false
        PokemonTeam.updatePokemon(pokemon)
    }

}
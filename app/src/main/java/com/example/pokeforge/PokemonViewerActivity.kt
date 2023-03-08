package com.example.pokeforge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pokeforge.databinding.ActivityPokemonViewerBinding
import com.example.pokeforge.pojo.PokemonAPI
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PokemonViewerActivity : AppCompatActivity() {
    private lateinit var statArr1 : ArrayList<String>
    private lateinit var statArr2 : ArrayList<String>
    private lateinit var binding: ActivityPokemonViewerBinding
    private lateinit var pokemon: Pokemon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pokemon = intent.getSerializableExtra("pokemon") as Pokemon
        Log.d("poke", pokemon.toString())
        GlobalScope.launch {
            pokemon.stats = getStatsOf(pokemon.dna[0], pokemon.dna[1])
            pokemon.types = getTypeOf(pokemon)
            runOnUiThread() {
                bind(pokemon)
                binding.statPvNb.text = pokemon.stats[0].toString()
                binding.statPvDef.text = pokemon.stats[1].toString()
                binding.statPvDefSpe.text = pokemon.stats[2].toString()
                binding.statPvAttack.text = pokemon.stats[3].toString()
                binding.statPvAttackSpe.text = pokemon.stats[4].toString()
                binding.statPvSpeed.text = pokemon.stats[5].toString()
                Log.d("poke", pokemon.toString())

            }






        }
        //bind(pokemon)

    }

    private fun bind(pokemon: Pokemon) {
        // Bind infos
        binding.pokemonName.text = pokemon.name
        // Bind types
        val type1 = pokemon.types[0]
        val bitmap1 = BitmapFactory.decodeResource(resources, getTypeSprite(type1))
        val resizedBitmap1 = Bitmap.createScaledBitmap(bitmap1, 180, 180, false)
        binding.pokemonTypeSprite1.setImageBitmap(resizedBitmap1)
        if (pokemon.types.size > 1) {
            var type2 = pokemon.types[1]
            // Rescale the second type sprite
            val bitmap2 = BitmapFactory.decodeResource(resources, getTypeSprite(type2))
            val resizedBitmap2 = Bitmap.createScaledBitmap(bitmap2, 180, 180, false)
            binding.pokemonTypeSprite2.setImageBitmap(resizedBitmap2)
        } else {
            // Delete the second type sprite
            binding.pokemonTypeSprite2.setImageResource(0)
        }

        // Bind sprite
        APISpritesClient().getSpriteImage(pokemon.dna[0], pokemon.dna[1], this) { bitmap ->
            runOnUiThread {
                if (bitmap != null) {
                    binding.pokemonSprite.setImageBitmap(bitmap)
                } else {
                    System.out.println("Erreur lors du chargement de l'image")
                }
            }
        }

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



    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private suspend fun getStatById(id: Int): ArrayList<Int>? {
        var list = ArrayList<Int>()
        val pokemonRes = APIClient.apiService
        val result = try {
            pokemonRes.doGetListResources(id)?.stats
        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: ${e.toString()}")
            null
        }
        for (i in result?.indices!!) {
            Log.d("TAG", "getStatsOf: ${result[i].base_stat}")
            result[i].base_stat?.let { list.add(it) }
        }
        return list
    }

    private suspend fun getStatsOf(id1:Int, id2:Int) : ArrayList<Int> {
        val pokemonStat1 = getStatById(id1)
        val pokemonStat2 = getStatById(id2)
        val intPokemonStat1 = ArrayList<Int>()
        val intPokemonStat2 = ArrayList<Int>()
        val finalPokemonStat = ArrayList<Int>()
        pokemonStat1?.forEach { stat ->
            intPokemonStat1.add(stat.toInt())
        }
        pokemonStat2?.forEach { stat ->
            intPokemonStat2.add(stat.toInt())
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
            val res2 = pokemonRes.doGetListType(pokemon.dna[1])?.types
            if (res2 != null) {
                    var resType = res2[0].type
                    var name = resType?.name
                    when(name){
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
                    var resType = res[0].type
                    var name = resType?.name
                    when(name){
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
            Log.d("TAG", "getStatsOf: ${e.toString()}")
            null
        }
        Log.d("TAG", "getTypeOf: ${types[0]} ${types[1]}")
        return types



    }


}
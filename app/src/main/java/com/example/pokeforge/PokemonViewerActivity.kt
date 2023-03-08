package com.example.pokeforge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.pokeforge.APIClient.client
import com.example.pokeforge.databinding.ActivityMainBinding
import com.example.pokeforge.databinding.ActivityPokemonViewerBinding
import com.example.pokeforge.pojo.MultipleResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonViewerBinding
    private lateinit var pokemon: Pokemon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pokemon = intent.getSerializableExtra("pokemon") as Pokemon

        bind(pokemon)
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
}
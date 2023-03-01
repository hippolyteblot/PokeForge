package com.example.pokeforge

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        binding.pokemonName.text = pokemon.name

        APISpritesClient().getSpriteImage(pokemon.dna[0], pokemon.dna[1]) { bitmap ->
            runOnUiThread {
                if (bitmap != null) {
                    binding.pokemonSprite.setImageBitmap(bitmap)
                } else {
                    System.out.println("Erreur lors du chargement de l'image")
                }
            }
        }



    }
}
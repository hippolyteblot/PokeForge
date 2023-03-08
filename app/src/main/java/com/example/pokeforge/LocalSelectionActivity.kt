package com.example.pokeforge.com.example.pokeforge

import NearbyManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeforge.*
import com.example.pokeforge.databinding.ActivityRemoteSelectionBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import java.nio.charset.Charset


class LocalSelectionActivity : AppCompatActivity() {

    private var binding: ActivityRemoteSelectionBinding? = null
    private lateinit var pokemons: ArrayList<Pokemon>

    private var selectedPokemon: Pokemon? = null
    private var lastItemView: View? = null

    private var endpointId: String = "0"
    private lateinit var connectionsClient: ConnectionsClient

    private lateinit var remoteSprite : ImageView

    private var firstSlot = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoteSelectionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        remoteSprite = binding!!.remotePokemon

        val recyclerView = binding!!.recyclerView


        pokemons = ArrayList()

        val random1 = (0..250).random()
        val random2 = (0..250).random()
        pokemons.add(Pokemon("Bulbasaur", 1, mutableListOf(), 7, 69, 64, listOf(45, 49, 49, 65, 65, 45), 5, listOf(9, 26)))
        pokemons.add(Pokemon("Ivysaur", 2,mutableListOf(),  16, 130, 142, listOf(60, 62, 63, 80, 80, 60), 5, listOf(6, 9)))
        pokemons.add(Pokemon("Charmeon", 5,mutableListOf(),  16, 142, 142, listOf(58, 64, 58, 80, 65, 80), 5, listOf(9, 6)))
        pokemons.add(Pokemon("Charizard", 6,mutableListOf(),  36, 240, 240, listOf(78, 84, 78, 109, 85, 100), 5, listOf(random1, random2)))
        pokemons.add(Pokemon("Squirtle", 7, mutableListOf(), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8,mutableListOf(),  16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9,mutableListOf(),  36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))

        pokemons.add(Pokemon("Squirtle", 7, mutableListOf(), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8,mutableListOf(),  16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9,mutableListOf(),  36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))

        recyclerView.adapter = PokemonAdapter(this, pokemons, this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        binding!!.yourPokemon.setOnClickListener {
            if (selectedPokemon != null) {
                // Purple200
                it.setBackgroundColor(resources.getColor(R.color.purple_200))
                binding!!.remotePokemon.setBackgroundColor(resources.getColor(R.color.white))
                firstSlot = true
            }
        }
        binding!!.yourPokemon.setBackgroundColor(resources.getColor(R.color.purple_200))

        binding!!.remotePokemon.setOnClickListener {
            if (selectedPokemon != null) {
                it.setBackgroundColor(resources.getColor(R.color.purple_200))
                binding!!.yourPokemon.setBackgroundColor(resources.getColor(R.color.white))
                firstSlot = false
            }
        }


    }

    fun setSelectedPokemon(pokemon: Pokemon) {
        selectedPokemon = pokemon
        if (firstSlot) {
            APISpritesClient.setSpriteImage(pokemon.dna, binding!!.yourPokemon, this)
        } else {
            APISpritesClient.setSpriteImage(pokemon.dna, binding!!.remotePokemon, this)
        }
    }


    fun getSelectedPokemon(): Pokemon? {
        return selectedPokemon
    }

    fun setLastItemView(view: View?) {
        lastItemView = view
    }

    fun getLastItemView(): View? {
        return lastItemView
    }
}
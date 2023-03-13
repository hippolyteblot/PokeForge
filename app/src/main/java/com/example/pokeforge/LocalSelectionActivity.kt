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

    private lateinit var remoteSprite : ImageView

    private var firstSlot = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoteSelectionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        remoteSprite = binding!!.remotePokemon

        val recyclerView = binding!!.recyclerView


        pokemons = PokemonTeam.getTeam()

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
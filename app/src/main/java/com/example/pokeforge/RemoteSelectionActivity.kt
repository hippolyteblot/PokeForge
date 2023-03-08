package com.example.pokeforge.com.example.pokeforge

import NearbyManager
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeforge.*
import com.example.pokeforge.databinding.ActivityRemoteSelectionBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import java.nio.charset.Charset


class RemoteSelectionActivity : AppCompatActivity() {

    private var binding: ActivityRemoteSelectionBinding? = null
    private lateinit var pokemons: ArrayList<Pokemon>

    private var selectedPokemon: Pokemon? = null
    private var lastItemView: View? = null

    private var endpointId: String = "0"
    private lateinit var connectionsClient: ConnectionsClient

    private lateinit var remoteSprite : ImageView
    private var remoteDna : List<Int> = listOf(-1, -1)
    private var remoteValidation : Boolean = false
    private var localValidation : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("RemoteSelectionActivity.onCreate()")

        binding = ActivityRemoteSelectionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        remoteSprite = binding!!.remotePokemon

        val recyclerView = binding!!.recyclerView

        NearbyManager.setActivity(this)

        pokemons = ArrayList()

        val random1 = (0..250).random()
        val random2 = (0..250).random()
        pokemons.add(Pokemon("Bulbasaur", 1, listOf(PokemonType.BUG, PokemonType.DRAGON), 7, 69, 64, listOf(45, 49, 49, 65, 65, 45), 5, listOf(9, 26)))
        pokemons.add(Pokemon("Ivysaur", 2, listOf(PokemonType.FLYING), 16, 130, 142, listOf(60, 62, 63, 80, 80, 60), 5, listOf(6, 9)))
        pokemons.add(Pokemon("Charmeon", 5, listOf(PokemonType.WATER, PokemonType.FIRE), 16, 142, 142, listOf(58, 64, 58, 80, 65, 80), 5, listOf(9, 6)))
        pokemons.add(Pokemon("Charizard", 6, listOf(PokemonType.BUG, PokemonType.DRAGON), 36, 240, 240, listOf(78, 84, 78, 109, 85, 100), 5, listOf(random1, random2)))
        pokemons.add(Pokemon("Squirtle", 7, listOf(PokemonType.BUG, PokemonType.DRAGON), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8, listOf(PokemonType.BUG, PokemonType.DRAGON), 16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9, listOf(PokemonType.BUG, PokemonType.DRAGON), 36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))

        pokemons.add(Pokemon("Squirtle", 7, listOf(PokemonType.BUG, PokemonType.DRAGON), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8, listOf(PokemonType.BUG, PokemonType.DRAGON), 16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9, listOf(PokemonType.BUG, PokemonType.DRAGON), 36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))

        recyclerView.adapter = PokemonAdapter(this, pokemons, this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Get the Nearby Connections client
        endpointId = intent.getStringExtra("connectionId").toString()
        connectionsClient = Nearby.getConnectionsClient(this)
        val message = "Hello from the other side"
        val payload = Payload.fromBytes(message.toByteArray(Charset.defaultCharset()))
        connectionsClient.sendPayload(endpointId, payload)

        binding!!.button.setOnClickListener {
            val dna = selectedPokemon?.dna ?: listOf(-1, -1)
            if (dna.size > 1) {
                sendData("validate:${dna[0]},${dna[1]}")
            } else {
                sendData("validate:${dna[0]}")
            }

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.loading_screen)
            val msg = "En attente de l'autre joueur..."
            dialog.findViewById<TextView>(R.id.message).text = msg

            dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
                dialog.dismiss()
                sendData("invalidate")
                localValidation = false
            }

            dialog.show()
            localValidation = true

            if (remoteValidation) {
                dialog.dismiss()
                print("Both players validated")
            }
        }
    }

    private fun sendData(message: String) {
        println("Sending a payload")
        connectionsClient.sendPayload(
            endpointId,
            Payload.fromBytes(message.toByteArray(Charset.defaultCharset()))
        )
    }


    fun setSelectedPokemon(pokemon: Pokemon?) {
        selectedPokemon = pokemon
        if (pokemon == null) {
            return
        }
        // Bind sprite
        val imageUrl = "http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=${pokemon.dna[0]}&id2=${pokemon.dna[1]}"
        binding?.yourPokemon?.let {
            Glide.with(this)
                .load(imageUrl)
                .into(it)
        }

        val dna1 = pokemon.dna[0].toString()
        var dna2 = ""
        if (pokemon.dna.size > 1) {
            dna2 = "," + pokemon.dna[1].toString()
        }
        println("Pokemon selected : $dna1$dna2")
        sendData("newPokemonSelected:$dna1$dna2")
    }

    fun updateRemoteSelection(dna: List<Int>) {
        APISpritesClient.setSpriteImage(dna, remoteSprite, this)
        remoteDna = dna
        println("Pokemon changed: $dna")
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

    fun setRemoteValidation(validate : Boolean) {
        remoteValidation = validate
        if (localValidation && remoteValidation) {
            print("Both players validated")
        }
    }
}
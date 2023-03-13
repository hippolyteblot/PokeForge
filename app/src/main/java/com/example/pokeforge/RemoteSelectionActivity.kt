package com.example.pokeforge.com.example.pokeforge

import NearbyManager
import android.app.Dialog
import android.content.Intent
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

        pokemons = PokemonTeam.getTeam()

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
            val intent = Intent(this, MakeFusionActivity::class.java)
            val dna1a = selectedPokemon?.dna?.get(0) ?: -1
            val dna1b = selectedPokemon?.dna?.get(1) ?: -1
            val dna1 : String = if (dna1b == -1) {
                dna1a.toString()
            } else {
                "$dna1a,$dna1b"
            }
            val dna2a = remoteDna[0]
            val dna2b = remoteDna[1]
            val dna2 : String = if (dna2b == -1) {
                dna2a.toString()
            } else {
                "$dna2a,$dna2b"
            }
            intent.putExtra("dna1", dna1)
            intent.putExtra("dna2", dna2)
            startActivity(intent)
        }
    }
}
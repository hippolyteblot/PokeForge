package com.example.pokeforge

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeforge.databinding.ActivityRemoteSelectionBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        pokemons = PokemonTeam.getTeamWthoutEgg()

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
                if (localValidation && remoteValidation) {
                    val dna1 = selectedPokemon?.dna
                    val dna2 = remoteDna
                    if (dna1 != null) {
                        fusion(dna1, dna2)
                    }
                }
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


    fun setRemoteValidation(validate : Boolean) {
        remoteValidation = validate
        if (localValidation && remoteValidation) {
            val dna1 = selectedPokemon?.dna
            val dna2 = remoteDna
            if (dna1 != null) {
                fusion(dna1, dna2)
            }
        }
    }

    private fun fusion(dna1 : List<Int>, dna2 :List<Int> ) {

        // if dna1[1] is 0 then dnaA = dna1[0], else random between 0 and 1
        val dnaA = if (dna1[1] == 0) {
            dna1[0]
        } else {
            listOf(0, 1).shuffled()[0]
        }
        val dnaB = if (dna2[1] == 0) {
            dna2[0]
        } else {
            listOf(0, 1).shuffled()[0]
        }

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.label_image_dialog)
        dialog.findViewById<ImageButton>(R.id.accept).setOnClickListener {
            val egg = hashMapOf(
                "name" to "",
                "dna" to listOf(dnaA, dnaB).shuffled(),
                "income" to 0,
                "owner" to FirebaseAuth.getInstance().currentUser!!.uid,
                "egg" to true,
            )
            val db = Firebase.firestore
            db.collection("pokemons").add(egg)

            removeFusionItem()
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun removeFusionItem() {
        val db = Firebase.firestore
        // 1. Get the number of items in the collection
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val number = document.data?.get("fusionItems") as Long
                    db.collection("users").document(uid).update("fusionItems", number - 1)
                }
            }
    }
}
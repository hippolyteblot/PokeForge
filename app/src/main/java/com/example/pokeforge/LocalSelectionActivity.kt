package com.example.pokeforge.com.example.pokeforge

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeforge.*
import com.example.pokeforge.databinding.ActivityRemoteSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LocalSelectionActivity : AppCompatActivity() {

    private var binding: ActivityRemoteSelectionBinding? = null
    private lateinit var pokemons: ArrayList<Pokemon>

    private var selectedPokemon: Pokemon? = null
    private var lastItemView: View? = null

    private var dna1 = 0
    private var dna2 = 0

    private lateinit var remoteSprite : ImageView

    private var firstSlot = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoteSelectionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        remoteSprite = binding!!.remotePokemon

        val recyclerView = binding!!.recyclerView


        pokemons = PokemonTeam.getTeamWthoutEgg()

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
                val dna2a = selectedPokemon!!.dna[0]
                val dna2b = selectedPokemon!!.dna[1]
                dna2 = dna2a

                if(dna2b != 0) {
                    // random number between 0 and 1
                    val random = (0..1).random()
                    dna2 = selectedPokemon!!.dna[random]
                }
                println("DNA2: $dna2")
            }
        }

        binding!!.button.setOnClickListener() {
            if (selectedPokemon != null) {
                val dialog = Dialog(this)
                val db = Firebase.firestore
                var totalincome : Long = 0
                var dnaBool = false
                var dna2Bool = false
                db.collection("pokemons").get().addOnSuccessListener() { result ->
                    result.forEach() { document ->
                        for (i in document.data["dna"] as List<Int>) {
                            if (document.data["owner"] == FirebaseAuth.getInstance().currentUser!!.uid) {
                                Log.d("TAG", "$i => ${document.data["dna"]} => $dna1 aaaaaaand ${document.data["dna"].toString() == dna1.toString()}")
                                if (i.toString() == dna1.toString() && !dnaBool) {
                                    dnaBool = true
                                    totalincome += document.data["income"] as Long
                                }
                                if (i.toString() == dna2.toString() && !dna2Bool) {
                                    dna2Bool = true
                                    totalincome += document.data["income"] as Long

                                }
                            }


                        }

                    }
                }
                Log.d("TAG", "Total income: $totalincome")
                val total = totalincome*1.2

                dialog.setContentView(R.layout.label_image_dialog)
                dialog.findViewById<Button>(R.id.accept).setOnClickListener {
                    val egg = hashMapOf(
                        "name" to "",
                        "dna" to listOf(dna1, dna2).shuffled(),
                        "income" to totalincome,
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
        }
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

    fun setSelectedPokemon(pokemon: Pokemon) {
        selectedPokemon = pokemon
        val dna1a = selectedPokemon!!.dna[0]
        val dna1b = selectedPokemon!!.dna[1]
        var dna = dna1a

        if(dna1b != 0) {
            // random number between 0 and 1
            val random = (0..1).random()
            dna = selectedPokemon!!.dna[random]
        }
        if (firstSlot) {
            APISpritesClient.setSpriteImage(pokemon.dna, binding!!.yourPokemon, this)
            dna1 = dna
        } else {
            APISpritesClient.setSpriteImage(pokemon.dna, binding!!.remotePokemon, this)
            dna2 = dna
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
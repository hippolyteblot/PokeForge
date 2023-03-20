package com.example.pokeforge.com.example.pokeforge

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeforge.*
import com.example.pokeforge.databinding.ActivityRemoteSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Suppress("unused")
class LocalSelectionActivity : AppCompatActivity() {

    private var binding: ActivityRemoteSelectionBinding? = null
    private lateinit var pokemons: ArrayList<Pokemon>

    private var selectedPokemon: Pokemon? = null
    private var lastItemView: View? = null

    private var dna1 = 0
    private var dna2 = 0
    private var pokemon1 : Pokemon? = null
    private var pokemon2 : Pokemon? = null

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
                it.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
                binding!!.remotePokemon.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                firstSlot = true

            }
        }
        binding!!.yourPokemon.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))

        binding!!.remotePokemon.setOnClickListener {
            if (selectedPokemon != null) {
                it.setBackgroundColor(ContextCompat.getColor(this,R.color.purple_200))
                binding!!.yourPokemon.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                firstSlot = false
                val dna2a = selectedPokemon!!.dna[0]
                val dna2b = selectedPokemon!!.dna[1]
                dna2 = dna2a

                if(dna2b != 0) {
                    // random number between 0 and 1
                    val random = (0..1).random()
                    dna2 = selectedPokemon!!.dna[random]
                }
            }
        }

        binding!!.button.setOnClickListener() {
            if (selectedPokemon != null) {
                val dialog = Dialog(this)
                val db = Firebase.firestore
                var totalincome : Long = 0
                var dnaBool = false
                var dna2Bool = false
                @Suppress("UNCHECKED_CAST")
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

                dialog.setContentView(R.layout.label_image_dialog)
                dialog.findViewById<ImageButton>(R.id.accept).setOnClickListener {
                    val egg = hashMapOf(
                        "name" to "",
                        "dna" to listOf(dna1, dna2).shuffled(),
                        "income" to totalincome,
                        "owner" to FirebaseAuth.getInstance().currentUser!!.uid,
                        "egg" to true,
                    )
                    val database = Firebase.firestore
                    database.collection("pokemons").add(egg).addOnSuccessListener {
                        Log.d("TAG", "DocumentSnapshot added with ID: ${it.id}")
                    }.addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }

                    lifecycleScope.launchWhenStarted {
                        removeFusionItem()
                        dialog.dismiss()
                        finish()
                    }

                }
                dialog.show()

            }
        }
    }

    suspend private fun removeFusionItem() {
        val db = Firebase.firestore
        // 1. Get the number of items in the collection
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        try {
            val number = db.collection("users").document(uid).get().await().data?.get("fusionItems") as Long
            db.collection("users").document(uid).update("fusionItems", number - 1)
        } catch (e: Exception) {
            Log.d("TAG", "Error getting documents: ", e)
        }
    }

    fun setSelectedPokemon(pokemon: Pokemon) {
        if (firstSlot && pokemon2 == pokemon) {
            return
        } else if (!firstSlot && pokemon1 == pokemon) {
            return
        }
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
            pokemon1 = pokemon
        } else {
            APISpritesClient.setSpriteImage(pokemon.dna, binding!!.remotePokemon, this)
            dna2 = dna
            pokemon2 = pokemon
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
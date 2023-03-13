package com.example.pokeforge

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokeforge.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var userUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userUID = intent.getStringExtra("userUID").toString()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setBalance()

        binding.claimPokepiece.setOnClickListener {
            claimPokepiece()
        }

    }

    fun startPokemonViewerActivity(pokemon: Pokemon) {
        val intent = Intent(this, PokemonViewerActivity::class.java)
        intent.putExtra("pokemon", pokemon)
        Log.d("poke", pokemon.toString())
        startActivity(intent)
    }

    fun claimPokepiece() {
        // Connect to firebase
        val db = Firebase.firestore
        // Get the last time the user claimed his pokepiece in the users collection where uuid = userUID
        val docRef = db.collection("users").document(userUID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val lastClaimed = document.data?.get("lastClaimed")
                    var balance = document.data?.get("balance").toString().toInt()
                    Log.d("poke", "lastClaimed: $lastClaimed")

                    if (lastClaimed != null) {
                        if (lastClaimed.toString().toLong() < System.currentTimeMillis() - 60000) {
                            // Get all the pokemon from the pokemon collection pokemons where owner = userUID
                            var total = 0
                            val docRef = db.collection("pokemons").whereEqualTo("owner", userUID)
                            docRef.get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        Log.d("poke", "${document.id} => ${document.data}")
                                        total += document.data["income"].toString().toInt()
                                        println("total: $total")
                                    }
                                    // Get the time spent from the last time the user claimed his pokepiece
                                    val timeSpent =
                                        System.currentTimeMillis() - lastClaimed.toString().toLong()
                                    val timeInHours = timeSpent / 60000
                                    val pokepieces = timeInHours * total
                                    balance += pokepieces.toInt()
                                    val userDocRef = db.collection("users").document(userUID)
                                    userDocRef.update("balance", balance)
                                        .addOnSuccessListener {
                                            val dialog = Dialog(this)
                                            dialog.setContentView(R.layout.label_image_dialog)
                                            val label = "$pokepieces pokepièces récupérées!"
                                            dialog.findViewById<TextView>(R.id.label).text = label
                                            dialog.findViewById<ImageView>(R.id.image)
                                                .setImageResource(R.drawable.pokepiece)
                                            dialog.findViewById<Button>(R.id.accept)
                                                .setOnClickListener {
                                                    dialog.dismiss()
                                                }
                                            dialog.show()
                                        }
                                        .addOnFailureListener { e -> println("Error updating document $e") }
                                    // Update the lastClaimed time
                                    userDocRef.update("lastClaimed", System.currentTimeMillis())
                                        .addOnSuccessListener {
                                            println("DocumentSnapshot successfully updated!")
                                        }
                                        .addOnFailureListener { e -> println("Error updating document $e") }
                                }

                                binding.balance.text = balance.toString()
                            }

                    }
                } else {
                    Log.d("poke", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }

    }

    fun setBalance() {
        // Connect to firebase
        val db = Firebase.firestore
        // Get the last time the user claimed his pokepiece in the users collection where uuid = userUID
        val docRef = db.collection("users").document(userUID)
        var balance = 0
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    balance = document.data?.get("balance").toString().toInt()
                } else {
                    Log.d("poke", "No such document")
                }
                binding.balance.text = balance.toString()
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
    }

}
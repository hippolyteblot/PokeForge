package com.example.pokeforge

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokeforge.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var userUID: String
    lateinit var userName: String
    lateinit var userSprite: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userUID = Firebase.auth.currentUser?.uid.toString()
        println("userUID: $userUID")
        getUserInfo()

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
        setCandies()
        setFusion()


        binding.claimPokepiece.setOnClickListener {
            claimPokepiece()
        }

    }

    fun startPokemonViewerActivity(pokemon: Pokemon) {
        val intent = Intent(this, PokemonViewerActivity::class.java)
        intent.putExtra("pokemon", pokemon)
        intent.putExtra("userUID", userUID)
        Log.d("poke", pokemon.toString())
        startActivity(intent)
    }

    private fun claimPokepiece() {
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
                            val pokemonsByOwner = db.collection("pokemons").whereEqualTo("owner", userUID)
                            pokemonsByOwner.get()
                                .addOnSuccessListener { documents ->
                                    for (doc in documents) {
                                        if(!(doc.data["egg"] as Boolean))
                                            total += doc.data["income"].toString().toInt()
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
                                            dialog.findViewById<ImageButton>(R.id.accept)
                                                .setOnClickListener {
                                                    dialog.dismiss()
                                                }
                                            dialog.show()
                                            if(balance/1000000000 > 0) {
                                                val newBalance = "${balance/1000000000}B"
                                                binding.balance.text = newBalance
                                            } else if (balance/1000000 > 0) {
                                                val newBalance = "${balance/1000000}M"
                                                binding.balance.text = newBalance
                                            } else if (balance/1000 > 0) {
                                                val newBalance = "${balance/1000}K"
                                                binding.balance.text = newBalance
                                            } else if (balance < 1000) {
                                                binding.balance.text = balance.toString()
                                            }
                                        }
                                        .addOnFailureListener { e -> println("Error updating document $e") }
                                    // Update the lastClaimed time
                                    userDocRef.update("lastClaimed", System.currentTimeMillis())
                                        .addOnSuccessListener {
                                            println("DocumentSnapshot successfully updated!")
                                        }
                                        .addOnFailureListener { e -> println("Error updating document $e") }
                                }

                            }
                        else {
                            val toast = Toast.makeText(
                                applicationContext,
                                "Vous devez attendre 1 minute avant de pouvoir réclamer à nouveau vos pokepièces",
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
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

    private fun setBalance() {
        // Connect to firebase
        val db = Firebase.firestore
        // Get the last time the user claimed his pokepiece in the users collection where uuid = userUID
        val docRef = db.collection("users").document(userUID)
        var balance = 0
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data?.get("balance") != null) {
                    balance = document.data?.get("balance").toString().toInt()

                    if(balance/1000000000 > 0) {
                        val newBalance = "${balance/1000000000}B"
                        binding.balance.text = newBalance
                    } else if (balance/1000000 > 0) {
                        val newBalance = "${balance/1000000}M"
                        binding.balance.text = newBalance
                    } else if (balance/1000 > 0) {
                        val newBalance = "${balance/1000}K"
                        binding.balance.text = newBalance
                    } else if (balance < 1000) {
                        binding.balance.text = balance.toString()
                    }
                } else {
                    Log.d("poke", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
    }

    fun setCandies() {
        // Connect to firebase
        val db = Firebase.firestore
        // Get the last time the user claimed his pokepiece in the users collection where uuid = userUID
        val docRef = db.collection("users").document(userUID)
        var candies = 0
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data?.get("candyItems") != null) {
                    candies = document.data?.get("candyItems").toString().toInt()
                } else {
                    Log.d("poke", "No such document")
                }
                binding.candyNumb.text = candies.toString()
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
    }

    fun setFusion() {
        // Connect to firebase
        val db = Firebase.firestore
        // Get the last time the user claimed his pokepiece in the users collection where uuid = userUID
        val docRef = db.collection("users").document(userUID)
        var fusion = 0
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data?.get("fusionItems") != null) {
                    fusion = document.data?.get("fusionItems").toString().toInt()
                } else {
                    Log.d("poke", "No such document")
                }
                binding.fusionNumb.text = fusion.toString()
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
    }

    private fun getUserInfo() {
        // Get the user's name and sprite from firebase
        val db = Firebase.firestore
        val docRef = db.collection("users").document(userUID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.data?.get("name").toString()
                    userSprite = document.data?.get("sprite").toString()
                } else {
                    Log.d("poke", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("poke", "get failed with ", exception)
            }
    }

}
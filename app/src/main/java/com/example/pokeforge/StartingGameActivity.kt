package com.example.pokeforge

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.databinding.ActivityStartingGameBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StartingGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartingGameBinding
    private var sprite: Int = R.drawable.character1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartingGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStartersSprites()
        println("Starting game")
        binding.changeSprite.setOnClickListener {
            val dialog = Dialog(this)
            // Use the layout "change_sprite_dialog" to create the dialog

            val dialogView = layoutInflater.inflate(R.layout.change_sprite_dialog, null)
            // Do the previous lines in one

            val rscList = arrayListOf(R.drawable.character1, R.drawable.character2, R.drawable.character3, R.drawable.character4, R.drawable.character5, R.drawable.character6, R.drawable.character7, R.drawable.character8)
            val adapter = SpriteSelectionAdapter(this, rscList)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter

            val button = dialogView.findViewById<Button>(R.id.validate_button)
            button.setOnClickListener {
                sprite = adapter.getSelectedSprite()
                println("Sprite selected: $sprite")
                println("Id of the sprite: ${R.drawable.character1}")
                binding.playerSprite.setImageResource(sprite)
                // hide the dialog
                dialog.dismiss()
            }
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
            dialog.setContentView(dialogView)
            dialog.show()
        }

        binding.startGame.setOnClickListener {
            addUserToDatabase()
            val intent = Intent(this, MainActivity::class.java)
            val adapter = binding.starterList.adapter as StarterSelectionAdapter
            intent.putExtra("starter", adapter.selectedDna.get(0))
            intent.putExtra("userUID", intent.getStringExtra("userUID"))
            startActivity(intent)
        }

    }

    private fun addUserToDatabase() {
        val db = Firebase.firestore
        val userUID = intent.getStringExtra("userUID")
        val user = hashMapOf(
            "name" to binding.playerName.text.toString(),
            "sprite" to sprite,
            "pokepieces" to 0,
        )
        if (userUID != null) {
            db.collection("users").document(userUID).set(user)
        }
        // add starter pokemon to the database
        val starter = hashMapOf(
            "name" to "Bulbasaur",
            "dna" to arrayListOf(1,0),
            "owner" to userUID
        )
        db.collection("pokemons").add(starter)

    }

    private fun setupStartersSprites() {
        val starters = ArrayList<ArrayList<Int>>()
        starters.add(arrayListOf(1,0))
        starters.add(arrayListOf(4,0))
        starters.add(arrayListOf(7,0))
        starters.add(arrayListOf(152,0))
        starters.add(arrayListOf(155,0))
        starters.add(arrayListOf(158,0))

        /*
        APISpritesClient.setSpriteImage(starters[0], binding.pokemon1, this)
        APISpritesClient.setSpriteImage(starters[1], binding.pokemon2, this)
        APISpritesClient.setSpriteImage(starters[2], binding.pokemon3, this)
        APISpritesClient.setSpriteImage(starters[3], binding.pokemon4, this)
        APISpritesClient.setSpriteImage(starters[4], binding.pokemon5, this)
        APISpritesClient.setSpriteImage(starters[5], binding.pokemon6, this)
         */
        binding.starterList.adapter = StarterSelectionAdapter(this, starters)
        binding.starterList.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
    }
}
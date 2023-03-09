package com.example.pokeforge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pokeforge.databinding.ActivityMakeFusionBinding

class MakeFusionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakeFusionBinding
    private lateinit var pokemon: Pokemon
    private lateinit var dna1: ArrayList<Int>
    private lateinit var dna2: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakeFusionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val strDna1 = intent.getStringExtra("dna1")
        val strDna2 = intent.getStringExtra("dna2")
        dna1 = strDna1!!.split(",").map { it.toInt() } as ArrayList<Int>
        dna2 = strDna2!!.split(",").map { it.toInt() } as ArrayList<Int>

        val fusionnedDna = ArrayList<Int>()
        // dna1[0], dna2[1]
        fusionnedDna.add(dna1[0])
        fusionnedDna.add(dna2[1])

        APISpritesClient.setSpriteImage(fusionnedDna, binding.pokemonSprite, this)




    }
}


package com.example.pokeforge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.pokeforge.APIClient.client
import com.example.pokeforge.databinding.ActivityMainBinding
import com.example.pokeforge.databinding.ActivityPokemonViewerBinding
import com.example.pokeforge.pojo.MultipleResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeFusionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakeFusionBinding
    private lateinit var pokemon: Pokemon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
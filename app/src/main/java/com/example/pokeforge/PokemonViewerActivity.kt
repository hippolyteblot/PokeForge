package com.example.pokeforge

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.pokeforge.databinding.ActivityPokemonViewerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


class PokemonViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPokemonViewerBinding
    private lateinit var pokemon: Pokemon
    private lateinit var userId : String
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pokemon = intent.getSerializableExtra("pokemon") as Pokemon

        println(pokemon.dna)
        userId = intent.getStringExtra("userUID").toString()
        binding = ActivityPokemonViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        APISpritesClient.setSpriteImage(pokemon.dna, binding.pokemonSprite, this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        if(pokemon.isEgg) {
            openEgg()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }



        GlobalScope.launch {
            pokemon.stats = getStatsOf(pokemon.dna[0], pokemon.dna[1])
            pokemon.types = getTypeOf(pokemon)
            pokemon.weight = getWeightOf(pokemon)
            pokemon.height = getHeightOf(pokemon)
            //pokemon.name = getNameOf(pokemon)
            runOnUiThread {
                bind(pokemon)
                binding.statPvNb.text = pokemon.stats[0].toString()
                binding.statPvDef.text = pokemon.stats[1].toString()
                binding.statPvDefSpe.text = pokemon.stats[2].toString()
                binding.statPvAttack.text = pokemon.stats[3].toString()
                binding.statPvAttackSpe.text = pokemon.stats[4].toString()
                binding.statPvSpeed.text = pokemon.stats[5].toString()
                binding.pokemonName.text = pokemon.name
                binding.pokemonHeight.text = pokemon.height.toString() + "0 cm"
                binding.income.text = pokemon.income.toString()
                var res = pokemon.weight.toString()
                if (res.length > 1) {
                    res = res.substring(0, res.length - 1) + "." + res.substring(res.length - 1)
                }
                binding.pokemonWeight.text = "$res kg"

            }

        }

        // Check if the pokemon can evolve
        lifecycleScope.launch {
            val evolution = getEvolution()
            if (evolution != null) {
                binding.evolveButton.setOnClickListener {
                    val dialog = AlertDialog.Builder(this@PokemonViewerActivity)
                    dialog.setTitle("Evolution")
                    dialog.setMessage("Voulez vous utiliser un super bonbon pour faire évoluer votre ${pokemon.name} ?")
                    dialog.setPositiveButton("Oui") { _, _ ->
                        lifecycleScope.launch {
                            if(hasCandyItems()) {
                                val db = Firebase.firestore
                                val docRef = db.collection("users").document(userId)
                                val candyItems = docRef.get().await().get("candyItems") as Long
                                docRef.update("candyItems", candyItems - 1)
                                pokemon.dna = listOf(evolution, 0)
                                evolveInDb(evolution)
                                val intent = Intent(this@PokemonViewerActivity, PokemonViewerActivity::class.java)
                                intent.putExtra("pokemon", pokemon)
                                intent.putExtra("userUID", userId)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                Toast.makeText(this@PokemonViewerActivity, "Vous n'avez pas de super bonbon", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                    dialog.setNegativeButton("Non") { _, _ -> }
                    dialog.show()


                }

                binding.evolveButton.visibility = ImageView.VISIBLE
            }
        }
    }

    private suspend fun hasCandyItems() : Boolean {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(userId)
        var hasAdnPoint = false
        try {
            val user = docRef.get().await()
            val nbItems = user.get("candyItems") as Long
            println("nbItems : $nbItems")
            if(nbItems > 0) {
                hasAdnPoint = true
            }
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
        }

        return hasAdnPoint
    }

    private fun evolveInDb(evolution: Int) {
        val db = Firebase.firestore
        val docRef = db.collection("pokemons").document(pokemon.id)
        docRef.update("dna", listOf(evolution, 0))
    }

    private fun renameInDb(newName: String) {
        val db = Firebase.firestore
        val docRef = db.collection("pokemons").document(pokemon.id)
        docRef.update("name", newName)
    }

    private suspend fun getEvolution() : Int? {
        val pokemonRes = APIClient.apiService
        val chainLink = pokemonRes.doGetEvolutionLink(pokemon.dna[0])?.evolutionChain
        val evolutionChain = pokemonRes.doGetEvolutionChain(chainLink?.url.toString().split("/").get(6).toInt())

        val id1 = evolutionChain?.chain?.species?.url?.split("/")?.get(6)?.toInt()
        val id2 = ArrayList<Int>()
        val id3 = ArrayList<Int>()
        if (evolutionChain?.chain?.evolvesTo?.size!! > 0) {
            for (i in 0 until evolutionChain.chain!!.evolvesTo.size) {
                // If number less than 251
                val id = evolutionChain.chain!!.evolvesTo.get(i).species?.url?.split("/")?.get(6)?.toInt()
                if (id != null) {
                    if (id < 251) {
                        id2.add(id)
                    }
                }
            }
            if (evolutionChain?.chain?.evolvesTo?.get(0)?.evolvesTo?.size!! > 0) {
                for (i in 0 until evolutionChain.chain!!.evolvesTo.get(0).evolvesTo.size) {
                    val id = evolutionChain.chain!!.evolvesTo.get(0).evolvesTo.get(i).species?.url?.split("/")?.get(6)?.toInt()
                    if (id != null) {
                        if (id < 251) {
                            id3.add(id)
                        }
                    }
                }
            }
        }

        println("id1 : $id1, id2 : $id2, id3 : $id3")
        if (id1 == pokemon.dna[0] && id2.contains(pokemon.dna[0]+1)) {
            val random = (0 until id2.size).random()
            println("random : $random, id2 : ${id2.get(random)}")
            return id2.get(random)
        } else if (id2.contains(pokemon.dna[0]) && id3.contains(pokemon.dna[0]+1)) {
            val random = (0 until id3.size).random()
            return id3.get(random)
        } else {
            return null
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun openEgg() {
        val dialog = AlertDialog.Builder(this)
        // use the layout label_image_input_dialog.xml
        val dialogView = layoutInflater.inflate(R.layout.label_image_input_dialog, null)
        dialog.setView(dialogView)
        val input = dialogView.findViewById<EditText>(R.id.input)
        input.inputType = InputType.TYPE_CLASS_TEXT

        input.setText(pokemon.name)
        val sprite = dialogView.findViewById<ImageView>(R.id.image)
        APISpritesClient.setSpriteImage(pokemon.dna, sprite, this)
        dialog.setPositiveButton("OK") { dialog, which ->
            pokemon.name = input.text.toString()
            binding.pokemonName.text = pokemon.name
            val db = Firebase.firestore
            val docRef = db.collection("pokemons").document(pokemon.id)
            docRef.update("name", pokemon.name)
            docRef.update("egg", false)
        }
        dialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        dialog.show()
    }

    private fun bind(pokemon: Pokemon) {
        // Bind info
        binding.pokemonName.text = pokemon.name
        // Bind types
        val type1 = pokemon.types[0]
        val bitmap1 = BitmapFactory.decodeResource(resources, getTypeSprite(type1))
        val resizedBitmap1 = Bitmap.createScaledBitmap(bitmap1, 180, 180, false)
        binding.pokemonTypeSprite1.setImageBitmap(resizedBitmap1)
        if (pokemon.types.size > 1) {
            val type2 = pokemon.types[1]
            // Rescale the second type sprite
            val bitmap2 = BitmapFactory.decodeResource(resources, getTypeSprite(type2))
            val resizedBitmap2 = Bitmap.createScaledBitmap(bitmap2, 180, 180, false)
            binding.pokemonTypeSprite2.setImageBitmap(resizedBitmap2)
        } else {
            // Delete the second type sprite
            binding.pokemonTypeSprite2.setImageResource(0)
        }
    }

    private fun getTypeSprite(type: PokemonType) : Int {
        return when (type) {
            PokemonType.NORMAL -> R.drawable.type_normal
            PokemonType.FIRE -> R.drawable.type_fire
            PokemonType.WATER -> R.drawable.type_water
            PokemonType.ELECTRIC -> R.drawable.type_electric
            PokemonType.GRASS -> R.drawable.type_grass
            PokemonType.ICE -> R.drawable.type_ice
            PokemonType.FIGHTING -> R.drawable.type_fighting
            PokemonType.POISON -> R.drawable.type_poison
            PokemonType.GROUND -> R.drawable.type_ground
            PokemonType.FLYING -> R.drawable.type_flying
            PokemonType.PSYCHIC -> R.drawable.type_psychic
            PokemonType.BUG -> R.drawable.type_bug
            PokemonType.ROCK -> R.drawable.type_rock
            PokemonType.GHOST -> R.drawable.type_ghost
            PokemonType.DRAGON -> R.drawable.type_dragon
            PokemonType.DARK -> R.drawable.type_dark
            PokemonType.STEEL -> R.drawable.type_steel
            PokemonType.FAIRY -> R.drawable.type_fairy
            else -> {
                R.drawable.type_normal
            }
        }
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        binding.apply {
                            Log.d("TAG", "getLocation: ${list[0].longitude}")
                            Log.d("TAG", "getLocation: ${list[0].latitude}")
                            val db = Firebase.firestore
                            db.collection("users").document(userId).update("longitude", list[0].longitude)
                                .addOnSuccessListener {
                                    Log.d("TAG", "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e  )}
                            db.collection("users").document(userId).update("latitude", list[0].latitude)
                                .addOnSuccessListener {
                                    Log.d("TAG", "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e  )}
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
    private suspend fun getStatById(id: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        val pokemonRes = APIClient.apiService
        val result = try {
            pokemonRes.doGetListResources(id)?.stats
        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
            null
        }
        for (i in result?.indices!!) {
            Log.d("TAG", "getStatsOf: ${result[i].baseStat}")
            result[i].baseStat?.let { list.add(it) }
        }
        return list
    }

    private suspend fun getStatsOf(dna1:Int, dna2:Int) : ArrayList<Int> {
        val id1 = dna1
        var id2 = dna2
        if (dna2 == 0) {
            id2 = id1
        }
        val pokemonStat1 = getStatById(id1)
        val pokemonStat2 = getStatById(id2)
        val intPokemonStat1 = ArrayList<Int>()
        val intPokemonStat2 = ArrayList<Int>()
        val finalPokemonStat = ArrayList<Int>()
        pokemonStat1.forEach { stat ->
            intPokemonStat1.add(stat)
        }
        pokemonStat2.forEach { stat ->
            intPokemonStat2.add(stat)
        }
        for (i in intPokemonStat1.indices) {
            finalPokemonStat.add((intPokemonStat1[i] + intPokemonStat2[i]) / 2)
        }



        return finalPokemonStat
    }

    private suspend fun getTypeOf(pokemon: Pokemon): MutableList<PokemonType> {
        val types : MutableList<PokemonType> = mutableListOf()
        val pokemonRes = APIClient.apiService
        try {
            if (pokemon.dna[1] == 0){
                val res = pokemonRes.doGetListType(pokemon.dna[0])?.types
                if (res != null) {
                    for (i in res.indices) {
                        val resType = res[i].type
                        when (resType?.name) {
                            "normal" -> types.add(PokemonType.NORMAL)
                            "fire" -> types.add(PokemonType.FIRE)
                            "water" -> types.add(PokemonType.WATER)
                            "electric" -> types.add(PokemonType.ELECTRIC)
                            "grass" -> types.add(PokemonType.GRASS)
                            "ice" -> types.add(PokemonType.ICE)
                            "fighting" -> types.add(PokemonType.FIGHTING)
                            "poison" -> types.add(PokemonType.POISON)
                            "ground" -> types.add(PokemonType.GROUND)
                            "flying" -> types.add(PokemonType.FLYING)
                            "psychic" -> types.add(PokemonType.PSYCHIC)
                            "bug" -> types.add(PokemonType.BUG)
                            "rock" -> types.add(PokemonType.ROCK)
                            "ghost" -> types.add(PokemonType.GHOST)
                            "dragon" -> types.add(PokemonType.DRAGON)
                            "dark" -> types.add(PokemonType.DARK)
                            "steel" -> types.add(PokemonType.STEEL)
                            "fairy" -> types.add(PokemonType.FAIRY)
                            else -> {
                                types.add(PokemonType.UNKNOWN)
                            }
                        }
                    }
                } else {
                    types.add(PokemonType.UNKNOWN)
                }
                return types
            } else {
                val res = pokemonRes.doGetListType(pokemon.dna[0])?.types
                val res2 = pokemonRes.doGetListType(pokemon.dna[1])?.types
                if (res2 != null) {
                    val resType = res2[0].type
                    when (resType?.name) {
                        "normal" -> types.add(PokemonType.NORMAL)
                        "fire" -> types.add(PokemonType.FIRE)
                        "water" -> types.add(PokemonType.WATER)
                        "electric" -> types.add(PokemonType.ELECTRIC)
                        "grass" -> types.add(PokemonType.GRASS)
                        "ice" -> types.add(PokemonType.ICE)
                        "fighting" -> types.add(PokemonType.FIGHTING)
                        "poison" -> types.add(PokemonType.POISON)
                        "ground" -> types.add(PokemonType.GROUND)
                        "flying" -> types.add(PokemonType.FLYING)
                        "psychic" -> types.add(PokemonType.PSYCHIC)
                        "bug" -> types.add(PokemonType.BUG)
                        "rock" -> types.add(PokemonType.ROCK)
                        "ghost" -> types.add(PokemonType.GHOST)
                        "dragon" -> types.add(PokemonType.DRAGON)
                        "dark" -> types.add(PokemonType.DARK)
                        "steel" -> types.add(PokemonType.STEEL)
                        "fairy" -> types.add(PokemonType.FAIRY)
                        else -> {
                            types.add(PokemonType.UNKNOWN)
                        }

                    }
                } else {
                    types.add(PokemonType.UNKNOWN)
                }
                if (res != null) {
                    val resType = res[0].type
                    when (resType?.name) {
                        "normal" -> types.add(PokemonType.NORMAL)
                        "fire" -> types.add(PokemonType.FIRE)
                        "water" -> types.add(PokemonType.WATER)
                        "electric" -> types.add(PokemonType.ELECTRIC)
                        "grass" -> types.add(PokemonType.GRASS)
                        "ice" -> types.add(PokemonType.ICE)
                        "fighting" -> types.add(PokemonType.FIGHTING)
                        "poison" -> types.add(PokemonType.POISON)
                        "ground" -> types.add(PokemonType.GROUND)
                        "flying" -> types.add(PokemonType.FLYING)
                        "psychic" -> types.add(PokemonType.PSYCHIC)
                        "bug" -> types.add(PokemonType.BUG)
                        "rock" -> types.add(PokemonType.ROCK)
                        "ghost" -> types.add(PokemonType.GHOST)
                        "dragon" -> types.add(PokemonType.DRAGON)
                        "dark" -> types.add(PokemonType.DARK)
                        "steel" -> types.add(PokemonType.STEEL)
                        "fairy" -> types.add(PokemonType.FAIRY)
                        else -> {
                            types.add(PokemonType.UNKNOWN)
                        }

                    }
                } else {
                    types.add(PokemonType.UNKNOWN)
                }

                if (types[0] == types[1]) {
                    types.removeAt(1)
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")

        }
        //Log.d("TAG", "getTypeOf: ${types[0]} ${types[1]}")
        return types



    }
    private suspend fun getWeightOf(pokemon: Pokemon): Int {
        var weight : String? = null
        val pokemonRes = APIClient.apiService
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            weight = if (pokemonRes.doGetListInfos(pokemon.dna[0]) != null) {
                pokemonRes.doGetListInfos(pokemon.dna[0])?.weight
            } else {
                pokemonRes.doGetListInfos(pokemon.dna[1])?.weight
            }

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return weight?.toInt() ?: 0
    }

    private suspend fun getHeightOf(pokemon: Pokemon): Int {
        var height : String? = null
        val pokemonRes = APIClient.apiService
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            height = if (pokemonRes.doGetListInfos(pokemon.dna[0]) != null) {
                pokemonRes.doGetListInfos(pokemon.dna[0])?.height
            } else {
                pokemonRes.doGetListInfos(pokemon.dna[1])?.height
            }

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return height?.toInt() ?: 0
    }

    private suspend fun getNameOf(pokemon: Pokemon): String {
        val pokemonRes = APIClient.apiService
        var name : String? = null
        try {
            pokemonRes.doGetListInfos(pokemon.dna[0])
            val name0 = pokemonRes.doGetListInfos(pokemon.dna[0])?.name
            //name0 first char to upper case and the rest to lower case
            val name0UpperCase = pokemonRes.doGetListInfos(pokemon.dna[0])?.name?.substring(0,1)
                ?.uppercase(
                    Locale.ROOT
                ) + pokemonRes.doGetListInfos(pokemon.dna[0])?.name?.substring(1)
                ?.lowercase(Locale.ROOT)
            name = name0UpperCase + "/" + pokemonRes.doGetListInfos(pokemon.dna[1])?.name?.substring(0,1)
                ?.uppercase(Locale.ROOT) + pokemonRes.doGetListInfos(pokemon.dna[1])?.name?.substring(1)
                ?.lowercase(Locale.ROOT)
            //

        } catch (e: Exception) {
            Log.d("TAG", "getStatsOf: $e")
        }
        return name ?: "Unknown"
    }


}
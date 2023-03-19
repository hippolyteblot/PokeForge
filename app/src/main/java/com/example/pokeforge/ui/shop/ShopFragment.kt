package com.example.pokeforge.ui.shop

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pokeforge.MainActivity
import com.example.pokeforge.R
import com.example.pokeforge.databinding.FragmentShopBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding:FragmentShopBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ShopViewModel::class.java)

        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        var offer1 = ""
        var offer2 = ""
        val types = arrayListOf<String>("normal", "fighting", "flying","fire","water","grass", "poison", "ground", "rock", "bug", "ghost", "steel", "electric", "psychic", "ice", "dragon", "dark")
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val db = Firebase.firestore
        val collectionRef = db.collection("date")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data.get("dateAc") != date) {
                        println(date)
                        println(document.data.get("dateAc"))

                        var egg1 = document.data.get("egg1")
                        var egg2 = document.data.get("egg2")
                        var rand1 = (0 until types.size).random()
                        var rand2 = (0 until types.size).random()
                        while (types[rand1] == egg1 || types[rand1] == egg2 || rand1 == rand2) {
                            rand1 = (0 until types.size).random()
                        }
                        while (types[rand2] == egg1 || types[rand2] == egg2 || rand1 == rand2) {
                            rand2 = (0 until types.size).random()
                        }

                        val newDailyInfo = hashMapOf(
                            "dateAc" to date,
                            "egg1" to types[rand1],
                            "egg2" to types[rand2]
                        )
                        var image1 = "egg_" + types[rand1]
                        var image2 = "egg_" + types[rand2]
                        var text1 = "text_" + types[rand1]
                        var text2 = "text_" + types[rand2]
                        offer1 = egg1.toString()
                        offer2 = egg2.toString()
                        binding.Offre1.setImageResource(resources.getIdentifier(image1, "drawable", activity?.packageName))
                        binding.Offre2.setImageResource(resources.getIdentifier(image2, "drawable", activity?.packageName))
                        binding.textOffre1.setImageResource(resources.getIdentifier(text1, "drawable", activity?.packageName))
                        binding.textOffre2.setImageResource(resources.getIdentifier(text2, "drawable", activity?.packageName))

                        collectionRef.document("dailyInfo").set(newDailyInfo)
                    } else {
                        var image1 = "egg_" + document.data.get("egg1").toString()
                        var image2 = "egg_" + document.data.get("egg2").toString()
                        var text1 = "text_" + document.data.get("egg1").toString()
                        var text2 = "text_" + document.data.get("egg2").toString()
                        offer1 = document.data.get("egg1").toString()
                        offer2 = document.data.get("egg2").toString()
                        binding.Offre1.setImageResource(resources.getIdentifier(image1, "drawable", activity?.packageName))
                        binding.Offre2.setImageResource(resources.getIdentifier(image2, "drawable", activity?.packageName))
                        binding.textOffre1.setImageResource(resources.getIdentifier(text1, "drawable", activity?.packageName))
                        binding.textOffre2.setImageResource(resources.getIdentifier(text2, "drawable", activity?.packageName))
                        println("meme date")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }


        binding.buttonItems.setOnClickListener {
            binding.bg.setImageResource(R.drawable.background_items)
            binding.bg.layoutParams.height = 2350
            binding.bg.requestLayout()
            binding.Offre1.visibility = View.GONE
            binding.Offre2.visibility = View.GONE
            binding.textOffre1.visibility = View.GONE
            binding.textOffre2.visibility = View.GONE
            binding.buyLegendaryButton.visibility = View.GONE
            binding.buyOffre1Button.visibility = View.GONE
            binding.buyOffre2Button.visibility = View.GONE
            binding.buyMysteryButton.visibility = View.GONE
            binding.buyJohtoButton.visibility = View.GONE
            binding.buyKantoButton.visibility = View.GONE
            binding.buyFireButton.visibility = View.GONE
            binding.buyWaterButton.visibility = View.GONE
            binding.buyGrassButton.visibility = View.GONE
            binding.buyAncientButton.visibility = View.GONE
            binding.buyCandyButton.visibility = View.VISIBLE
            binding.buyFusionButton.visibility = View.VISIBLE
            binding.buttonEggs.visibility = View.VISIBLE
            binding.buttonItems.visibility = View.GONE
        }

        binding.buttonEggs.setOnClickListener {
            binding.bg.setImageResource(R.drawable.background)
            binding.bg.layoutParams.height = 4900
            binding.bg.requestLayout()
            binding.Offre1.visibility = View.VISIBLE
            binding.Offre2.visibility = View.VISIBLE
            binding.textOffre1.visibility = View.VISIBLE
            binding.textOffre2.visibility = View.VISIBLE
            binding.buyLegendaryButton.visibility = View.VISIBLE
            binding.buyOffre1Button.visibility = View.VISIBLE
            binding.buyOffre2Button.visibility = View.VISIBLE
            binding.buyMysteryButton.visibility = View.VISIBLE
            binding.buyJohtoButton.visibility = View.VISIBLE
            binding.buyKantoButton.visibility = View.VISIBLE
            binding.buyFireButton.visibility = View.VISIBLE
            binding.buyWaterButton.visibility = View.VISIBLE
            binding.buyGrassButton.visibility = View.VISIBLE
            binding.buyAncientButton.visibility = View.VISIBLE
            binding.buyCandyButton.visibility = View.GONE
            binding.buyFusionButton.visibility = View.GONE
            binding.buttonEggs.visibility = View.GONE
            binding.buttonItems.visibility = View.VISIBLE
        }

        binding.buyCandyButton.setOnClickListener{
            buyItem(3000, "candyItems", "super bonbon")
        }

        binding.buyFusionButton.setOnClickListener{
            buyItem(4000, "fusionItems", "pointeau ADN")
        }

        binding.buyLegendaryButton.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmer l'achat")
            builder.setMessage("Voulez-vous acheter cet oeuf ?")
            builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                lifecycleScope.launch {
                    if(removeMoney(10000) == true) {
                        Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                        var pokeName = ""
                        var pokeId = 0
                        val db = Firebase.firestore
                        val collectionRef = db.collection("pokemon_available")

                        collectionRef.whereEqualTo("isMythical", true)
                            .get()
                            .addOnSuccessListener { documents ->
                                collectionRef.whereEqualTo("isLegendary", true)
                                    .get()
                                    .addOnSuccessListener { documents2 ->
                                        val listeLegend = documents.documents
                                        val listeMyth = documents2.documents
                                        val liste = listeLegend + listeMyth
                                        val taille = liste.size

                                        val random = (0 until taille).random()
                                        val pokemon = liste[random]
                                        println("DocumentSnapshot data: ${pokemon.data}")
                                        pokeName = pokemon.data?.get("name").toString()
                                        pokeId = pokemon.data?.get("id").toString().toInt()

                                        println(pokeName)

                                        val stat1 = pokemon.data?.get("stat1").toString().toInt()
                                        val stat2 = pokemon.data?.get("stat2").toString().toInt()
                                        val stat3 = pokemon.data?.get("stat3").toString().toInt()
                                        val stat4 = pokemon.data?.get("stat4").toString().toInt()
                                        val stat5 = pokemon.data?.get("stat5").toString().toInt()
                                        val stat6 = pokemon.data?.get("stat6").toString().toInt()
                                        //total
                                        val sum = stat1 + stat2 + stat3 + stat4 + stat5 + stat6
                                        val total = (sum.toFloat().pow(2)/300).toInt()

                                        println(total)
                                        val newPoke = hashMapOf(
                                            "name" to pokeName,
                                            "dna" to listOf(pokeId, 0),
                                            "egg" to true,
                                            "income" to total,
                                            "owner" to FirebaseAuth.getInstance().currentUser?.uid,
                                        )
                                        db.collection("pokemons").add(newPoke)
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting documents: ", exception)
                            }

                    }
                }


                })
            builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }

        binding.buyAncientButton.setOnClickListener{

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmer l'achat")
            builder.setMessage("Voulez-vous acheter cet oeuf ?")
            builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                lifecycleScope.launch {
                    if(removeMoney(5000)) {
                        Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                        var pokeName = ""
                        var pokeId = 0
                        val db = Firebase.firestore
                        val collectionRef = db.collection("pokemon_available")

                        collectionRef.whereLessThanOrEqualTo("capture_rate", 45)
                            .get()
                            .addOnSuccessListener { documents ->
                                val liste = documents.documents
                                val taille = liste.size
                                val random = (0 until taille).random()
                                val pokemon = liste[random]
                                println("DocumentSnapshot data: ${pokemon.data}")
                                pokeName = pokemon.data?.get("name").toString()
                                pokeId = pokemon.data?.get("id").toString().toInt()
                                println(pokeName)
                                val stat1 = pokemon.data?.get("stat1").toString().toInt()
                                val stat2 = pokemon.data?.get("stat2").toString().toInt()
                                val stat3 = pokemon.data?.get("stat3").toString().toInt()
                                val stat4 = pokemon.data?.get("stat4").toString().toInt()
                                val stat5 = pokemon.data?.get("stat5").toString().toInt()
                                val stat6 = pokemon.data?.get("stat6").toString().toInt()
                                //total
                                val sum = stat1 + stat2 + stat3 + stat4 + stat5 + stat6
                                val total = (sum.toFloat().pow(2)/300).toInt()
                                println(total)
                                val newPoke = hashMapOf(
                                    "name" to pokeName,
                                    "dna" to listOf(pokeId, 0),
                                    "egg" to true,
                                    "income" to total,
                                    "owner" to FirebaseAuth.getInstance().currentUser?.uid,
                                )
                                db.collection("pokemons").add(newPoke)
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting documents: ", exception)
                            }
                    }

                }
            })
            builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }

        binding.buyOffre1Button.setOnClickListener{
            buy(offer1, 1000)
        }

        binding.buyOffre2Button.setOnClickListener{
            buy(offer2, 2000)
        }

        binding.buyKantoButton.setOnClickListener{
            buyGene(1, 1000)
        }

        binding.buyJohtoButton.setOnClickListener{
            buyGene(2, 1000)
        }

        binding.buyMysteryButton.setOnClickListener{

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmer l'achat")
            builder.setMessage("Voulez-vous acheter cet oeuf ?")
            builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                lifecycleScope.launch {
                    if(removeMoney(1300) == true) {
                        Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                        var pokeName = ""
                        var pokeId = 0
                        val db = Firebase.firestore
                        val collectionRef = db.collection("pokemon_available")

                        collectionRef.whereEqualTo("isLegendary", false)
                            .whereEqualTo("isMythical", false)
                            .get()
                            .addOnSuccessListener { documents ->
                                val liste = documents.documents
                                val taille = liste.size
                                val random = (0 until taille).random()
                                val pokemon = liste[random]
                                println("DocumentSnapshot data: ${pokemon.data}")
                                pokeName = pokemon.data?.get("name").toString()
                                pokeId = pokemon.data?.get("id").toString().toInt()
                                println(pokeName)
                                val stat1 = pokemon.data?.get("stat1").toString().toInt()
                                val stat2 = pokemon.data?.get("stat2").toString().toInt()
                                val stat3 = pokemon.data?.get("stat3").toString().toInt()
                                val stat4 = pokemon.data?.get("stat4").toString().toInt()
                                val stat5 = pokemon.data?.get("stat5").toString().toInt()
                                val stat6 = pokemon.data?.get("stat6").toString().toInt()
                                //total
                                val sum = stat1 + stat2 + stat3 + stat4 + stat5 + stat6
                                val total = (sum.toFloat().pow(2)/300).toInt()

                                println(total)
                                val newPoke = hashMapOf(
                                    "name" to pokeName,
                                    "dna" to listOf(pokeId, 0),
                                    "egg" to true,
                                    "income" to total,
                                    "owner" to (activity as MainActivity).userUID,
                                )
                                db.collection("pokemons").add(newPoke)
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting documents: ", exception)
                            }
                    }
                }
            })
            builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }

        binding.buyFireButton.setOnClickListener{
            buy("fire", 1000)
        }

        binding.buyWaterButton.setOnClickListener{
            buy("water", 1000)
        }

        binding.buyGrassButton.setOnClickListener{
            buy("grass", 1000)
        }


        val main = activity as MainActivity
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun buy (offer: String, price: Long){
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmer l'achat")
            builder.setMessage("Voulez-vous acheter cet oeuf ?")
            builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                lifecycleScope.launch {
                    if(removeMoney(price)) {
                        Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                        var pokeName = ""
                        var pokeId = 0
                        val db = Firebase.firestore
                        val collectionRef = db.collection("pokemon_available")

                        collectionRef.whereArrayContains("types", offer)
                            .whereEqualTo("isLegendary", false)
                            .whereEqualTo("isMythical", false)
                            .get()
                            .addOnSuccessListener { documents ->
                                val liste = documents.documents
                                val taille = liste.size
                                val random = (0 until taille).random()
                                val pokemon = liste[random]
                                println("DocumentSnapshot data: ${pokemon.data}")
                                pokeName = pokemon.data?.get("name").toString()
                                pokeId = pokemon.data?.get("id").toString().toInt()
                                println(pokeName)
                                val stat1 = pokemon.data?.get("stat1").toString().toInt()
                                val stat2 = pokemon.data?.get("stat2").toString().toInt()
                                val stat3 = pokemon.data?.get("stat3").toString().toInt()
                                val stat4 = pokemon.data?.get("stat4").toString().toInt()
                                val stat5 = pokemon.data?.get("stat5").toString().toInt()
                                val stat6 = pokemon.data?.get("stat6").toString().toInt()
                                //total
                                val sum = stat1 + stat2 + stat3 + stat4 + stat5 + stat6
                                val total = (sum.toFloat().pow(2)/300).toInt()
                                println(total)

                                val newPoke = hashMapOf(
                                    "name" to pokeName,
                                    "dna" to listOf(pokeId, 0),
                                    "egg" to true,
                                    "income" to total,
                                    "owner" to (activity as MainActivity).userUID,
                                )
                                db.collection("pokemons").add(newPoke)
                            }
                            .addOnFailureListener { exception ->
                                Log.w("TAG", "Error getting documents: ", exception)
                            }
                    }
                }


            })
            builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }


    fun buyGene (generation: Int, price: Long){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmer l'achat")
        builder.setMessage("Voulez-vous acheter cet oeuf ?")
        builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
            lifecycleScope.launch {
                if(removeMoney(price)) {
                    Toast.makeText(activity, "Oeuf ajouté !", Toast.LENGTH_SHORT).show()
                    var pokeName = ""
                    var pokeId = 0
                    val db = Firebase.firestore
                    val collectionRef = db.collection("pokemon_available")

                    collectionRef.whereEqualTo("generation", generation)
                        .get()
                        .addOnSuccessListener { documents ->
                            val liste = documents.documents
                            val taille = liste.size
                            val random = (0 until taille).random()
                            val pokemon = liste[random]
                            println("DocumentSnapshot data: ${pokemon.data}")
                            pokeName = pokemon.data?.get("name").toString()
                            pokeId = pokemon.data?.get("id").toString().toInt()
                            println(pokeName)
                            val stat1 = pokemon.data?.get("stat1").toString().toInt()
                            val stat2 = pokemon.data?.get("stat2").toString().toInt()
                            val stat3 = pokemon.data?.get("stat3").toString().toInt()
                            val stat4 = pokemon.data?.get("stat4").toString().toInt()
                            val stat5 = pokemon.data?.get("stat5").toString().toInt()
                            val stat6 = pokemon.data?.get("stat6").toString().toInt()
                            //total
                            val sum = stat1 + stat2 + stat3 + stat4 + stat5 + stat6
                            val total = (sum.toFloat().pow(2)/300).toInt()
                            println(total)
                            val newPoke = hashMapOf(
                                "name" to pokeName,
                                "dna" to listOf(pokeId, 0),
                                "egg" to true,
                                "income" to total,
                                "owner" to (activity as MainActivity).userUID,
                            )
                            db.collection("pokemons").add(newPoke)
                        }
                        .addOnFailureListener { exception ->
                            Log.w("TAG", "Error getting documents: ", exception)
                        }
                }
            }

        })
        builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(activity, "Oeuf non ajouté !", Toast.LENGTH_SHORT).show()
        })
        builder.show()
    }

    fun buyItem(price: Long, field: String, item : String){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmer l'achat")
        builder.setMessage("Voulez-vous acheter un $item ?")
        builder.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
            lifecycleScope.launch {
                if(removeMoney(price) == true) {
                    Toast.makeText(activity, "$item ajouté !", Toast.LENGTH_SHORT).show()
                    val db = Firebase.firestore
                    val collectionRef = db.collection("users").document((activity as MainActivity).userUID)
                    collectionRef.update(field, FieldValue.increment(1))
                }
            }
        })
        builder.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(activity, "Achat annulé", Toast.LENGTH_SHORT).show()
        })
        builder.show()
    }

    suspend fun removeMoney(value: Long): Boolean {
        val db = Firebase.firestore
        var success = false
        var balance = 0L
        val collectionRef = db.collection("users").document((activity as MainActivity).userUID)

        try {
            val document = collectionRef.get().await()
            balance = document.data?.get("balance").toString().toLong()
            println(balance)

            if (balance >= value) {
                success = true
                collectionRef.update("balance", FieldValue.increment(-value)).await()
                balance -= value
                (activity as MainActivity).binding.balance.text = balance.toString()
                Log.d("TAG", "DocumentSnapshot successfully updated!")
            } else {
                Toast.makeText(activity, "Vous n'avez pas assez d'argent !", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            Log.d("TAG", "get failed with ", exception)
        }


        return success
    }


}
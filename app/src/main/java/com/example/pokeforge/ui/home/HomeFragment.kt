package com.example.pokeforge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.*
import com.example.pokeforge.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView


    private val team = PokemonTeam

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerView


        // Run on background thread loadPokemonsFromFirebase()
        loadPokemonsFromFirebase { list ->
            team.setTeam(list)
            recyclerView.adapter = PokemonAdapter(this.requireContext(), team.getTeam(), this.activity as MainActivity)
            recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadPokemonsFromFirebase(callback: (ArrayList<Pokemon>) -> Unit) {
        val db = Firebase.firestore
        val collectionRef = db.collection("pokemons")
        val pokemons = ArrayList<Pokemon>()
        val activity = requireActivity() as MainActivity
        collectionRef.whereEqualTo("owner", activity.userUID)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    println("DocumentSnapshot data: ${document.data}")
                    val dna = document.data["dna"] as List<*>
                    pokemons.add(Pokemon(
                        document.data.get("name") as String,
                        document.id,
                        mutableListOf(),
                        0,
                        0,
                        0,
                        listOf(),
                        if ( document.data.get("income") != null){
                            document.data.get("income").toString().toInt()
                        } else {
                            0
                        },
                        listOf(dna[0].toString().toInt(),
                            dna[1].toString().toInt()),
                        document.data.get("egg") as Boolean,
                    ))
                }
                callback(pokemons)
            }
            .addOnFailureListener { exception ->
                println("get failed $exception")
                callback(pokemons)
            }

    }

    fun updateAdapter() {
        loadPokemonsFromFirebase { list ->
            team.setTeam(list)
            recyclerView.adapter = PokemonAdapter(this.requireContext(), team.getTeam(), this.activity as MainActivity)
            recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        }

    }

    // On resume, update the adapter
    override fun onResume() {
        super.onResume()
        updateAdapter()
    }
}
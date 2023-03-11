package com.example.pokeforge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.MainActivity
import com.example.pokeforge.Pokemon
import com.example.pokeforge.PokemonAdapter
import com.example.pokeforge.databinding.FragmentHomeBinding
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Integer.parseInt


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView


    private lateinit var pokemons: ArrayList<Pokemon>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerView

        pokemons = ArrayList<Pokemon>()

        recyclerView.adapter = PokemonAdapter(this.requireContext(), pokemons, this.activity as MainActivity)
        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)


        // Run on background thread loadPokemonsFromFirebase()
        loadPokemonsFromFirebase { pokemons ->
            recyclerView.adapter = PokemonAdapter(this.requireContext(), pokemons, this.activity as MainActivity)
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
                        1,
                        mutableListOf(),
                        0,
                        0,
                        0,
                        listOf(),
                        5,
                        listOf(dna[0], dna[1]) as List<Int>
                    ))
                }
                callback(pokemons)
            }
            .addOnFailureListener { exception ->
                println("get failed $exception")
                callback(pokemons)
            }

    }
}
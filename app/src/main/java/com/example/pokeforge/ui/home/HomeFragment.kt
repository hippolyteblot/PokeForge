package com.example.pokeforge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.MainActivity
import com.example.pokeforge.Pokemon
import com.example.pokeforge.PokemonAdapter
import com.example.pokeforge.PokemonType
import com.example.pokeforge.databinding.FragmentHomeBinding

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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerView

        pokemons = ArrayList<Pokemon>()
        pokemons.add(Pokemon("Bulbasaur", 1, listOf(), 7, 69, 64, listOf(45, 49, 49, 65, 65, 45), 5, listOf(9, 26)))
        pokemons.add(Pokemon("Ivysaur", 2, listOf(), 16, 130, 142, listOf(60, 62, 63, 80, 80, 60), 5, listOf(6, 9)))
        pokemons.add(Pokemon("Charmeon", 5, listOf(PokemonType.WATER, PokemonType.FIRE), 16, 142, 142, listOf(58, 64, 58, 80, 65, 80), 5, listOf(9, 6)))
        pokemons.add(Pokemon("Charizard", 6, listOf(), 36, 240, 240, listOf(78, 84, 78, 109, 85, 100), 5, listOf(1, 5)))
        pokemons.add(Pokemon("Squirtle", 7, listOf(), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8, listOf(), 16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9, listOf(), 36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))

        pokemons.add(Pokemon("Squirtle", 7, listOf(), 5, 88, 64, listOf(44, 48, 65, 50, 64, 43), 5, listOf(132, 133)))
        pokemons.add(Pokemon("Wartortle", 8, listOf(), 16, 155, 142, listOf(59, 63, 80, 65, 80, 58), 5, listOf(133, 76)))
        pokemons.add(Pokemon("Blastoise", 9, listOf(), 36, 239, 240, listOf(79, 83, 100, 85, 105, 78), 5, listOf(6, 13)))
        recyclerView.adapter = PokemonAdapter(this.requireContext(), pokemons, this.activity as MainActivity)
        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
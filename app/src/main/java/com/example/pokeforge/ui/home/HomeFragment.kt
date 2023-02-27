package com.example.pokeforge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.MainActivity
import com.example.pokeforge.Pokemon
import com.example.pokeforge.PokemonAdapter
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
        pokemons.add(Pokemon("Bulbasaur", 1, listOf(), 7, 69, 64, listOf(45, 49, 49, 65, 65, 45), 5, listOf(8, 0)))
        pokemons.add(Pokemon("Ivysaur", 2, listOf(), 16, 130, 142, listOf(60, 62, 63, 80, 80, 60), 5, listOf(9, 0)))
        recyclerView.adapter = PokemonAdapter(this.requireContext(), pokemons, this.activity as MainActivity)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
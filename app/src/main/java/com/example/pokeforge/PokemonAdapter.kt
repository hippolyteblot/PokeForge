package com.example.pokeforge

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter (private val context: Context, private val contactList: List<Pokemon>, private val activity: MainActivity) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sprite = itemView.findViewById<ImageView>(R.id.sprite)
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val income = itemView.findViewById<TextView>(R.id.income)

        fun bind(pokemon: Pokemon) {
            var spriteName = "s" + pokemon.dna[0].toString()
            if (pokemon.dna[1] != 0) {
                spriteName += "_" + pokemon.dna[1].toString()
            }
            System.out.println("spriteee :" + spriteName)
            val rscId = context.resources.getIdentifier(spriteName, "drawable", context.packageName)
            System.out.println("rscId :" + rscId)
            // Get the image named s8.png in the drawable folder
            sprite.setImageResource(rscId)
            name.text = pokemon.name
            income.text = pokemon.income.toString() + " P/H"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = View.inflate(context, R.layout.item_pokemon, null)
        return PokemonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(contactList[position])
    }
}
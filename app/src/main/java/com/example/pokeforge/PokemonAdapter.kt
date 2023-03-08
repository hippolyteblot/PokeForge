package com.example.pokeforge

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.com.example.pokeforge.LocalSelectionActivity
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity

class PokemonAdapter (private val context: Context, private val contactList: List<Pokemon>, private val activity: Activity) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sprite = itemView.findViewById<ImageView>(R.id.sprite)
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val income = itemView.findViewById<TextView>(R.id.income)

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(pokemon: Pokemon) {

            APISpritesClient.setSpriteImage(pokemon.dna, sprite, context)


            name.text = pokemon.name
            income.text = pokemon.income.toString() + " P/H"

            itemView.setOnClickListener {
                // If activiy is of type MainActivity, we want to start PokemonViewerActivity
                if (activity is MainActivity)
                    activity.startPokemonViewerActivity(pokemon)
                if (activity is RemoteSelectionActivity) {
                    if (activity.getSelectedPokemon() == null) {
                        activity.setSelectedPokemon(pokemon)
                        activity.setLastItemView(itemView)
                        //itemView.setBackgroundColor(activity.getColor(R.color.purple_200))
                    } else if (activity.getSelectedPokemon() == pokemon) {
                        activity.setSelectedPokemon(null)
                        activity.setLastItemView(null)
                        //itemView.setBackgroundColor(activity.getColor(R.color.white))
                    } else {
                        //activity.getLastItemView()?.setBackgroundColor(activity.getColor(R.color.white))
                        activity.setSelectedPokemon(pokemon)
                        //itemView.setBackgroundColor(activity.getColor(R.color.purple_200))
                        activity.setLastItemView(itemView)
                    }
                } else if (activity is LocalSelectionActivity) {
                    activity.setSelectedPokemon(pokemon)
                }
            }
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
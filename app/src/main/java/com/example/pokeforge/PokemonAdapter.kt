package com.example.pokeforge

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.com.example.pokeforge.LocalSelectionActivity
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PokemonAdapter (private val context: Context, private val contactList: List<Pokemon>, private val activity: Activity) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sprite = itemView.findViewById<ImageView>(R.id.sprite)
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val income = itemView.findViewById<TextView>(R.id.income)

        @OptIn(DelicateCoroutinesApi::class)
        fun bind(pokemon: Pokemon) {

            if (!pokemon.isEgg) {
                name.text = pokemon.name
                income.text = pokemon.income.toString()
                APISpritesClient.setSpriteImage(pokemon.dna, sprite, context)
            } else {
                name.text = "Œuf"
                income.text = "?"
                sprite.setImageResource(R.drawable.egg)

            }

            itemView.setOnClickListener {
                // If activiy is of type MainActivity, we want to start PokemonViewerActivity
                if (activity is MainActivity) {
                    activity.startPokemonViewerActivity(pokemon)
                } else if (activity is RemoteSelectionActivity) {
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

    private suspend fun getNameByAPI(pokemon: Pokemon): String {
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
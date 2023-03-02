package com.example.pokeforge

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter (private val context: Context, private val contactList: List<String>, private val activity: LocalFusionActivity) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sprite = itemView.findViewById<ImageView>(R.id.sprite)
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val pokepieces = itemView.findViewById<TextView>(R.id.pokepieces)

        fun bind(player: String) {
            name.text = player
            pokepieces.text = "0 P"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = View.inflate(context, R.layout.item_player, null)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(contactList[position])

    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}
package com.example.pokeforge

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter (private val context: Context, private val contactList: List<Map<String,String>>, private val activity: RemoteFusionActivity) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val pokepieces = itemView.findViewById<TextView>(R.id.pokepieces)

        fun bind(player: String) {
            name.text = player
            pokepieces.text = "0 P"
        }

        init {
            itemView.setOnClickListener {
                contactList[adapterPosition].get("id")?.let { it1 -> activity.connectToPlayer(it1) }
                println("Requesting connection to player ${contactList[adapterPosition].get("id")}")
                // Display a loading screen
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.loading_screen)
                val msg = "En attente de l'autre joueur..."
                dialog.findViewById<TextView>(R.id.message).text = msg

                dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
                    dialog.dismiss()
                }


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = View.inflate(context, R.layout.item_player, null)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(contactList[position].get("name")!!)

    }

    override fun getItemCount(): Int {
        return contactList.size
    }

}
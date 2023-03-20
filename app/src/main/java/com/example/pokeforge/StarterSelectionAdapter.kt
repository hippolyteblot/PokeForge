package com.example.pokeforge

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class StarterSelectionAdapter (private val context: Context, private val spriteList: List<List<Int>>):
    RecyclerView.Adapter<StarterSelectionAdapter.SpriteViewHolder>() {

    private var lastItem : View ?= null
    var selectedDna = listOf<Int>()

    inner class SpriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dna = listOf<Int>()

        fun bind(dna: List<Int>) {
            APISpritesClient.setSpriteImage(dna, itemView.findViewById(R.id.sprite), context)
            this.dna = dna
        }

        init {
            itemView.setOnClickListener {
                itemView.findViewById<ImageView>(R.id.sprite).setBackgroundColor(context.getColor(R.color.purple_200))
                lastItem?.findViewById<ImageView>(R.id.sprite)?.setBackgroundColor(context.getColor(R.color.transparent))
                lastItem = itemView
                selectedDna = dna
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpriteViewHolder {
        val view = View.inflate(context, R.layout.item_sprite, null)
        return SpriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpriteViewHolder, position: Int) {
        holder.bind(spriteList[position])
    }

    override fun getItemCount(): Int {
        return spriteList.size
    }

}
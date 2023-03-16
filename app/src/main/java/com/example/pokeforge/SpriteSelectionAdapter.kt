package com.example.pokeforge

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SpriteSelectionAdapter (private val context: Context, private val spriteList: List<Int>):
    RecyclerView.Adapter<SpriteSelectionAdapter.SpriteViewHolder>() {

    private var lastItem : View ?= null
    protected var rscId = 0

    inner class SpriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var id = 0

        fun bind(rscId : Int) {
            itemView.findViewById<ImageView>(R.id.sprite).setImageResource(rscId)
            this.id = rscId
        }

        init {
            itemView.setOnClickListener {
                itemView.findViewById<ImageView>(R.id.sprite).setBackgroundColor(context.getColor(R.color.purple_200))
                lastItem?.findViewById<ImageView>(R.id.sprite)?.setBackgroundColor(Color.TRANSPARENT)
                lastItem = itemView
                rscId = id
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

    fun getSelectedSprite() : Int {
        // Return the resource id of the selected sprite
        return rscId
    }

}
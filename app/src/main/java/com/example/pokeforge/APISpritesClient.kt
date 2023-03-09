package com.example.pokeforge

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity
import java.io.File
import java.net.URL


object APISpritesClient {

    fun setSpriteImage(dna: List<Int>, imageView: ImageView, context: Context) {

        val dna1 = dna[0].toString()
        var dna2 = ""
        if (dna.size > 1) {
            dna2 = dna[1].toString()
        }

        // Check if there is a file named $dna1.$dna2.png in the local storage
        if(getSpriteFromStorage(dna1.toInt(), dna2.toInt(), context) != null) {
            imageView.let {
                Glide.with(it.context)
                    .load(getSpriteFromStorage(dna1.toInt(), dna2.toInt(), context))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(it)
            }
            println("Sprite loaded from local storage")
            return
        }

        val imageUrl = "http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=$dna1&id2=$dna2"

        //val image = URL(imageUrl).openStream().use { BitmapFactory.decodeStream(it) }
        //val filename = "$dna1.$dna2.png"
        //val file = File(context.filesDir, filename)
        //file.outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }

        imageView?.let {
            Glide.with(it.context)
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(it)
        }
        println("Sprite loaded from remote server")//4002
    }

    private fun getSpriteFromStorage(id1: Int, id2: Int, context: Context): Bitmap? {
        val filename = "$id1.$id2.png"
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.absolutePath)
        }
        return null
    }
}

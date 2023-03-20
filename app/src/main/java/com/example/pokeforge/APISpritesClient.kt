package com.example.pokeforge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import java.io.File
import java.io.FileOutputStream


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

        imageView.let {
            Glide.with(it.context)
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        val file = File(context.filesDir, "$dna1.$dna2.png")
                        val out = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

                        out.flush()
                        out.close()
                        return false
                    }
                })
                .into(it)
        }
        println("Sprite loaded from remote server")

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

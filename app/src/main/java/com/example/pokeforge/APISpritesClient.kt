package com.example.pokeforge

import android.graphics.Bitmap
import android.os.AsyncTask
import java.net.URL


class APISpritesClient {

    fun getSpriteImage(id1: Int, id2: Int, callback: (Bitmap?) -> Unit) {
        val url =
            URL("http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=$id1&id2=$id2")

        System.out.println("http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=$id1&id2=$id2")
        AsyncTask.execute {
            val inputStream = url.openStream()
            val bitmap = inputStream.use { input ->
                android.graphics.BitmapFactory.decodeStream(input)
            }
            if (bitmap != null) {
                callback(bitmap)
            } else {
                callback(null)
            }
            callback(bitmap)
        }
    }
}

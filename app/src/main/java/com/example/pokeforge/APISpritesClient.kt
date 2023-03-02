package com.example.pokeforge

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL


class APISpritesClient {

    fun getSpriteImage(id1: Int, id2: Int, activity: Activity, callback: (Bitmap?) -> Unit) {

        val savedSprite = loadSpriteFromStorage(id1, id2, activity)
        if (savedSprite != null) {
            callback(savedSprite)
            return
        }

        val url =
            URL("http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=$id1&id2=$id2")

        System.out.println("http://ec2-35-181-154-238.eu-west-3.compute.amazonaws.com/spriteGetter/?id1=$id1&id2=$id2")
        AsyncTask.execute {
            val inputStream = url.openStream()
            val bitmap = inputStream.use { input ->
                BitmapFactory.decodeStream(input)
            }
            if (bitmap != null) {
                // Save the sprite in the local storage
                activity.openFileOutput("$id1.$id2.png", Activity.MODE_PRIVATE).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                callback(bitmap)
            } else {
                callback(null)
            }
            callback(bitmap)
        }
    }

    fun loadSpriteFromStorage(id1: Int, id2: Int, activity: Activity): Bitmap? {
        // Check in the local storage if the sprite is already saved (id1.id2.png)
        for (file in activity?.filesDir?.listFiles()!!) {
            if (file.name == "$id1.$id2.png") {
                return BitmapFactory.decodeFile(file.absolutePath)
            }
        }
        return null
    }
}

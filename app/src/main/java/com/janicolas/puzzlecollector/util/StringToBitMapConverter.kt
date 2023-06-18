package com.janicolas.puzzlecollector.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.InputStream

object StringToBitMapConverter {
    fun StringToBitMap(base64: String): Bitmap{
        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val inputStream: InputStream = ByteArrayInputStream(decodedString)
        return BitmapFactory.decodeStream(inputStream)
    }
}
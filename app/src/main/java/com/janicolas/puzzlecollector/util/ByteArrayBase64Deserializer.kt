package com.janicolas.puzzlecollector.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import android.util.Base64

class ByteArrayBase64Deserializer : JsonDeserializer<ByteArray?> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ByteArray? {
        val base64String = json.asString
        return Base64.decode(base64String, Base64.DEFAULT)
    }
}
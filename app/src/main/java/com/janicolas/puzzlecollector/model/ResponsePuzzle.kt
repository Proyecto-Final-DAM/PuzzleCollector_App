package com.janicolas.puzzlecollector.model

import com.google.gson.annotations.SerializedName

data class ResponsePuzzle(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("puzzleImg") val puzzleImg: String,
    @SerializedName("type") val type: Int,
    @SerializedName("brand") val brand: String,
    @SerializedName("description") val description: String?,
    @SerializedName("linksPath") val linksPath: String
)

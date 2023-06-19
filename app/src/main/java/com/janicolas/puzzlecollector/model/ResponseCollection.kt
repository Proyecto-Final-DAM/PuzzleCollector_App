package com.janicolas.puzzlecollector.model

import com.google.gson.annotations.SerializedName

data class ResponseCollection(
    @SerializedName("user") var user:ResponseUser,
    @SerializedName("puzzle") var puzzle: ResponsePuzzle,
    @SerializedName("notes") var notes:String
)

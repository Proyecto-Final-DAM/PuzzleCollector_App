package com.janicolas.puzzlecollector.model

import com.google.gson.annotations.SerializedName

data class ResponseUser(
    @SerializedName("id") var Id: Long,
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("iconPath") var icon: String,
    @SerializedName("iconImg") var iconImg: String?,
    @SerializedName("admin") var admin: Boolean,
    @SerializedName("puzzles") var puzzles:MutableList<ResponsePuzzle>
)
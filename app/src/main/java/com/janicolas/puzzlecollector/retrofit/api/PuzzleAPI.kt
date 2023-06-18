package com.janicolas.puzzlecollector.retrofit.api

import com.janicolas.puzzlecollector.model.ResponsePuzzle
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PuzzleAPI {
    @GET("/puzzle/all")
    fun getPuzzles():Call<List<ResponsePuzzle>>

    @GET("/puzzle/{id}")
    fun getPuzzleById(@Path("id") id: Long): Call<ResponsePuzzle>

    @GET("/puzzle/price")
    fun getPuzzleByPrice(@Query("priceMin") priceMin: Double, @Query("priceMax") priceMax: Double):
            Call<List<ResponsePuzzle>>

    @GET("/puzzle/brand={brand}")
    fun getPuzzleByBrand(@Path("brand") brand:String): Call<List<ResponsePuzzle>>

    @GET("/puzzle/type={type}")
    fun getPuzzleByType(@Path("type") type: Int): Call<List<ResponsePuzzle>>
}
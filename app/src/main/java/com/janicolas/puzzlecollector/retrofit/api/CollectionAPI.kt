package com.janicolas.puzzlecollector.retrofit.api

import com.janicolas.puzzlecollector.model.ResponseCollection
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CollectionAPI {

    @GET("/collection/user")
    fun getCollectionByUserId(@Query("id") id:Long): Call<List<ResponseCollection>>

    @GET("/collection")
    fun getCollectionByUserIdAndPuzzleId(@Query("userId") userId:Long, @Query("puzzleId") puzzleId:Long):
            Call<ResponseCollection>

    @GET("/collection/exists")
    fun existsCollection(@Query("userId") userId:Long,
                         @Query("puzzleId") puzzleId: Long): Call<Boolean>

    @POST("/collection")
    fun createCollection(@Query("userId") userId:Long,
                         @Query("puzzleId") puzzleId: Long,
                         @Query("notes") notes: String): Call<ResponseCollection>

    @PUT("/collection")
    fun updateCollection(@Query("userId") userId:Long,
                         @Query("puzzleId") puzzleId: Long,
                         @Query("notes") notes: String): Call<ResponseCollection>

    @DELETE("/collection/delete")
    fun deleteCollection(@Query("userId") userId:Long,
                         @Query("puzzleId") puzzleId: Long): Call<Boolean>
}
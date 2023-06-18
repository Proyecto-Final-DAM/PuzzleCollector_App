package com.janicolas.puzzlecollector.retrofit.api

import com.janicolas.puzzlecollector.model.ResponseUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @GET("/user/username={name}")
    fun getUserByName(@Path("name") name: String): Call<ResponseUser>

    @GET("/user/exists")
    fun existsUser(@Query("username") username:String): Call<Boolean>

    @POST("/user")
    fun newUser(@Body user: ResponseUser): Call<ResponseUser>

    @PUT("/user")
    fun updateUser(@Body user: ResponseUser): Call<ResponseUser>

    @DELETE("/user/delete")
    fun deleteUser(@Query("id") id: Long): Call<Boolean>
}
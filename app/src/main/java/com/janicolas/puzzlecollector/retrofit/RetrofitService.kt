package com.janicolas.puzzlecollector.retrofit

import com.google.gson.GsonBuilder
import com.janicolas.puzzlecollector.util.ByteArrayBase64Deserializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class RetrofitService {

    private lateinit var retrofit: Retrofit

    fun getRetrofit(): Retrofit {
        // Crea un interceptor de registro de HTTP
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Crea una instancia de OkHttpClient con el interceptor
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(ByteArray::class.java, ByteArrayBase64Deserializer())
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.98:8080")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }
}
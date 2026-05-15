package com.example.finarch.network

import com.example.finarch.model.EventoCalendario
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CalendarioApi {
    @GET("finanzas")
    fun getEventos(): Call<List<EventoCalendario>>

    companion object {
        fun create(): CalendarioApi {
            return Retrofit.Builder()
                .baseUrl("https://69daaea726585bd92dd40a70.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CalendarioApi::class.java)
        }
    }
}
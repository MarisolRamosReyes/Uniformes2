package com.example.uniformes2

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/User/autenticar")
    fun autenticarUser(
        @Query("nombre") nombre: String,
        @Query("contrasena") contrasena: String
    ): Call<ResponseBody>

    // CRUD Escuela
    @POST("api/School")
    fun addEscuela(@Body escuela: EscuelaT): Call<List<EscuelaT>>

    @GET("api/School/GetActiveSchools")
    fun getAllEscuelas(): Call<List<EscuelaT>>

    @PUT("api/School/{id}")
    fun updateEscuela(@Path("id") id: Int, @Body escuela: EscuelaT): Call<EscuelaT>

    @PUT("api/School/inactivar/{id}")
    fun inactivarEscuela(@Path("id") id: Int): Call<ResponseBody>




    // CRUD Tallas
    @POST("api/Size")
    fun addTalla(@Body talla: TallasT): Call<List<TallasT>>

    @GET("api/Size/GetActiveSizes")
    fun getAllTallas(): Call<List<TallasT>>

    @PUT("api/Size/{id}")
    fun updateTalla(@Path("id") id: Int, @Body talla: TallasT): Call<TallasT>

    @PUT("api/Size/inactivar/{id}")
    fun inactivarTalla(@Path("id") id: Int): Call<ResponseBody>


    // CRUD Prendas
    @POST("api/Garment")
    fun addPrenda(@Body prenda: PrendasT): Call<List<PrendasT>>

    @GET("api/Garment/GetActiveGarments")
    fun getAllPrendas(): Call<List<PrendasT>>

    @PUT("api/Garment/{id}")
    fun updatePrenda(@Path("id") id: Int, @Body prenda: PrendasT): Call<PrendasT>

    @PUT("api/Garment/inactivar/{id}")
    fun inactivarPrenda(@Path("id") id: Int): Call<ResponseBody>
}
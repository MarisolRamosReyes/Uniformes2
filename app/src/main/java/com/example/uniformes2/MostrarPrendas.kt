package com.example.uniformes2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MostrarPrendas : ComponentActivity() {
    private lateinit var listViewPrendas: ListView
    private lateinit var btnIrCrud: Button
    private lateinit var apiService: ApiService
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrar_prenda)  // Asegúrate de que este layout tenga un elemento con id listViewPrendas

        listViewPrendas = findViewById(R.id.listViewPrendas)
        btnIrCrud = findViewById(R.id.btnIrCrud)
        btnVolver = findViewById(R.id.btnVolver)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Obtener todos las prendas
        obtenerTodosLasPrendas()

        // Configurar el botón para ir a la actividad CRUD
        btnIrCrud.setOnClickListener {
            val intent = Intent(this, PrendasCRUD::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this@MostrarPrendas, MenuPrincipalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerTodosLasPrendas() {
        val call = apiService.getAllPrendas()
        call.enqueue(object : Callback<List<PrendasT>> {
            override fun onResponse(call: Call<List<PrendasT>>, response: Response<List<PrendasT>>) {
                if (response.isSuccessful) {
                    val prendas = response.body() ?: emptyList()
                    val adapter = ArrayAdapter(
                        this@MostrarPrendas,
                        android.R.layout.simple_list_item_1,
                        prendas.map { prenda ->
                            "Tipo: ${prenda.type}, Descripción: ${prenda.description}"
                        }
                    )
                    listViewPrendas.adapter = adapter
                } else {
                    Log.e("API_ERROR", "Error al obtener las prendas: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MostrarPrendas, "Error al obtener las prendas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PrendasT>>, t: Throwable) {
                Log.e("API_ERROR", "Error de conexión: ${t.message}", t)
                Toast.makeText(this@MostrarPrendas, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val hostnameVerifier = HostnameVerifier { _, _ -> true }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(hostnameVerifier)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

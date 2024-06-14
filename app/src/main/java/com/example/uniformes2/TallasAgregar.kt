package com.example.uniformes2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TallasAgregar : AppCompatActivity() {
    private lateinit var editTextTalla: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var btnAddTalla: Button
    private lateinit var btnGetAllTalla: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_tallas)

        editTextTalla = findViewById(R.id.editTextTalla)
        editTextPrecio= findViewById(R.id.editTextPrecio)
        btnAddTalla = findViewById(R.id.btnAddTalla)
        btnGetAllTalla = findViewById(R.id.btnGetAllTallas)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddTalla.setOnClickListener {
            val talla = TallasT(
                idT = 0,
                size = editTextTalla.text.toString(),
                price = editTextPrecio.text.toString(),
                status = 1 // Asumimos 1 como estado activo
            )
            addTalla(talla)
        }

        btnGetAllTalla.setOnClickListener {
            val intent = Intent(this@TallasAgregar, MostrarTallas::class.java)
            startActivity(intent)
        }
    }

    private fun addTalla(talla: TallasT) {
        val call = apiService.addTalla(talla)
        call.enqueue(object : Callback<List<TallasT>> {
            override fun onResponse(call: Call<List<TallasT>>, response: Response<List<TallasT>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TallasAgregar, "Talla añadida", Toast.LENGTH_SHORT).show()
                    editTextTalla.text.clear()
                    editTextPrecio.text.clear()
                } else {
                    Toast.makeText(this@TallasAgregar, "Error al añadir talla", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TallasT>>, t: Throwable) {
                Toast.makeText(this@TallasAgregar, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

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

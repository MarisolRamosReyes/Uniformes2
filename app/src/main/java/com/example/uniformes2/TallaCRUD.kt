package com.example.uniformes2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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

class TallaCRUD : AppCompatActivity() {

    private lateinit var btnAddTalla: Button
    private lateinit var btnGetAllTallas: Button
    private lateinit var btnUpdateTalla: Button
    private lateinit var btnDeleteTalla: Button
    private lateinit var apiService: ApiService
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tallas)


        btnAddTalla = findViewById(R.id.btnAddTalla)
        btnGetAllTallas = findViewById(R.id.btnGetAllTallas)
        btnUpdateTalla = findViewById(R.id.btnUpdateTalla)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/") // Asegúrate de que la URL base sea correcta
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddTalla.setOnClickListener {
            val intent = Intent(this, TallasAgregar::class.java)
            startActivity(intent)
        }

        btnGetAllTallas.setOnClickListener {
            val intent = Intent(this@TallaCRUD, MostrarTallas::class.java)
            startActivity(intent)
        }
        btnUpdateTalla.setOnClickListener {
            val intent = Intent(this@TallaCRUD, TallasModificar::class.java)
            startActivity(intent)
        }
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
package com.example.uniformes2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class PrendasCRUD : AppCompatActivity() {
    private lateinit var btnAddPrendas: Button
    private lateinit var btnUpdatePrenda: Button
    private lateinit var btnGetAllPrendas: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prenda)

        btnAddPrendas = findViewById(R.id.btnAddPrendas)
        btnUpdatePrenda = findViewById(R.id.btnUpdatePrenda)
        btnGetAllPrendas = findViewById(R.id.btnGetAllPrendas)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        btnAddPrendas.setOnClickListener {
            val intent = Intent(this, PrendasAgregar::class.java)
            startActivity(intent)
        }

        btnUpdatePrenda.setOnClickListener {
            val intent = Intent(this, PrendasModificar::class.java)
            startActivity(intent)
        }

        btnGetAllPrendas.setOnClickListener {
            val intent = Intent(this, MostrarPrendas::class.java)
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

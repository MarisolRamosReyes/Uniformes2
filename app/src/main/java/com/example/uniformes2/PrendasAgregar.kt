package com.example.uniformes2

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
import android.content.Intent

class PrendasAgregar : AppCompatActivity() {
    private lateinit var editTextTipo: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var btnAddPrendas: Button
    private lateinit var btnGetAllPrendas: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_prendas)

        editTextTipo = findViewById(R.id.editTextTipo)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        btnAddPrendas = findViewById(R.id.btnAddPrendas)
        btnGetAllPrendas = findViewById(R.id.btnGetAllPrendas)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddPrendas.setOnClickListener {
            val prenda = PrendasT(
                idG = 0,
                type = editTextTipo.text.toString(),
                description = editTextDescripcion.text.toString(),
                Status = 1 // Asumimos 1 como estado activo
            )
            addPrenda(prenda)
        }
        btnGetAllPrendas.setOnClickListener {
            val intent = Intent(this@PrendasAgregar, MostrarPrendas::class.java)
            startActivity(intent)
        }
    }

    private fun addPrenda(prenda: PrendasT) {
        val call = apiService.addPrenda(prenda)
        call.enqueue(object : Callback<List<PrendasT>> {
            override fun onResponse(call: Call<List<PrendasT>>, response: Response<List<PrendasT>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PrendasAgregar, "Prenda añadida correctamente", Toast.LENGTH_SHORT).show()
                    editTextTipo.text.clear()
                    editTextDescripcion.text.clear()
                } else {
                    Toast.makeText(this@PrendasAgregar, "Error al añadir prenda", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PrendasT>>, t: Throwable) {
                Toast.makeText(this@PrendasAgregar, "Error de conexión", Toast.LENGTH_SHORT).show()
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


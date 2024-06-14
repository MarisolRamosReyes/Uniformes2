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

class EscuelaAgregar : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var btnAddEscuela: Button
    private lateinit var btnGetAllEscuelas: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_escuela)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        btnAddEscuela = findViewById(R.id.btnAddEscuela)
        btnGetAllEscuelas = findViewById(R.id.btnGetAllEscuelas)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnAddEscuela.setOnClickListener {
            val escuela = EscuelaT(
                idSc = 0,
                name = editTextNombre.text.toString(),
                address = editTextDireccion.text.toString(),
                phone = editTextTelefono.text.toString(),
                status = 1 // Asumimos 1 como estado activo
            )
            addEscuela(escuela)
        }

        btnGetAllEscuelas.setOnClickListener {
            val intent = Intent(this@EscuelaAgregar, MostrarEscuela::class.java)
            startActivity(intent)
        }
    }

    private fun addEscuela(escuela: EscuelaT) {
        val call = apiService.addEscuela(escuela)
        call.enqueue(object : Callback<List<EscuelaT>> {
            override fun onResponse(call: Call<List<EscuelaT>>, response: Response<List<EscuelaT>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EscuelaAgregar, "Escuela añadida", Toast.LENGTH_SHORT).show()
                    // Limpiar los campos después de añadir la escuela
                    editTextNombre.text.clear()
                    editTextDireccion.text.clear()
                    editTextTelefono.text.clear()
                } else {
                    Toast.makeText(this@EscuelaAgregar, "Error al añadir escuela", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<EscuelaT>>, t: Throwable) {
                Toast.makeText(this@EscuelaAgregar, "Error de conexión", Toast.LENGTH_SHORT).show()
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

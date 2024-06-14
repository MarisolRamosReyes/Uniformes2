package com.example.uniformes2

import android.app.AlertDialog
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

class TallasModificar : AppCompatActivity() {
    private lateinit var editTextId: EditText
    private lateinit var editTextTalla: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var btnUpdateTalla: Button
    private lateinit var btnGetAllTalla: Button
    private lateinit var btnDeleteTalla: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_tallas)

        editTextId = findViewById(R.id.editTextId)
        editTextTalla = findViewById(R.id.editTextTalla)
        editTextPrecio = findViewById(R.id.editTextPrecio)
        btnUpdateTalla = findViewById(R.id.btnUpdateTalla)
        btnGetAllTalla = findViewById(R.id.btnGetAllTallas)
        btnDeleteTalla = findViewById(R.id.btnDeleteTalla)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnGetAllTalla.setOnClickListener {
            val intent = Intent(this@TallasModificar, MostrarTallas::class.java)
            startActivity(intent)
        }

        btnUpdateTalla.setOnClickListener {
            val idString = editTextId.text.toString()
            val talla = editTextTalla.text.toString()
            val precio = editTextPrecio.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@TallasModificar, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto TallasT
            val tallaObj = TallasT(id, talla, precio, 1) // Establecer el status a 1 por defecto
            updateTalla(id, tallaObj)
        }

        btnDeleteTalla.setOnClickListener {
            val idStr = editTextId.text.toString().trim()
            if (idStr.isNotEmpty()) {
                val id = idStr.toIntOrNull()
                if (id != null) {
                    deleteTalla(id)
                } else {
                    Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTalla(id: Int, talla: TallasT) {
        val call = apiService.updateTalla(id, talla)
        call.enqueue(object : Callback<TallasT> {
            override fun onResponse(call: Call<TallasT>, response: Response<TallasT>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TallasModificar, "Talla modificada correctamente", Toast.LENGTH_SHORT).show()
                    editTextTalla.text.clear()
                    editTextPrecio.text.clear()
                } else {
                    Toast.makeText(this@TallasModificar, "Error al modificar la talla", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TallasT>, t: Throwable) {
                Toast.makeText(this@TallasModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteTalla(id: Int) {
        // Crear el diálogo de confirmación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta talla?")

        // Configurar el botón de confirmación
        builder.setPositiveButton("Sí") { dialog, _ ->
            val call = apiService.inactivarTalla(id)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TallasModificar, "Talla eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@TallasModificar, "Error al eliminada la talla", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TallasModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.dismiss()
        }

        // Configurar el botón de cancelación
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        builder.show()
    }

    // ...
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



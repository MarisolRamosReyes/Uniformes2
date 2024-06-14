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
import android.app.AlertDialog

class EscuelaModificar : AppCompatActivity() {
    private lateinit var editTextId: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var btnMostrarEscuelas: Button
    private lateinit var btnModificarEscuela: Button
    private lateinit var btnEliminarEscuela: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_escuela)

        editTextId = findViewById(R.id.editTextId)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        btnMostrarEscuelas = findViewById(R.id.btnGetAllEscuelas)
        btnModificarEscuela = findViewById(R.id.btnUpdateEscuela)
        btnEliminarEscuela = findViewById(R.id.btnDeleteEscuela)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnMostrarEscuelas.setOnClickListener {
            val intent = Intent(this@EscuelaModificar, MostrarEscuela::class.java)
            startActivity(intent)
        }

        btnModificarEscuela.setOnClickListener {
            val idString = editTextId.text.toString()
            val nombre = editTextNombre.text.toString()
            val direccion = editTextDireccion.text.toString()
            val telefono = editTextTelefono.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@EscuelaModificar, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Escuela
            val escuela = EscuelaT(id, nombre, direccion, telefono, 1) // Establecer el estado a 1 por defecto
            updateEscuela(id, escuela)
        }

        btnEliminarEscuela.setOnClickListener {
            val idString = editTextId.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@EscuelaModificar, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            deleteEscuela(id)
        }
    }

    // Función para modificar la escuela
    private fun updateEscuela(id: Int, escuela: EscuelaT) {
        val call = apiService.updateEscuela(id, escuela)
        call.enqueue(object : Callback<EscuelaT> {
            override fun onResponse(call: Call<EscuelaT>, response: Response<EscuelaT>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EscuelaModificar, "Escuela modificada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EscuelaModificar, "Error al modificar la escuela", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EscuelaT>, t: Throwable) {
                Toast.makeText(this@EscuelaModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para eliminar la escuela
    private fun deleteEscuela(id: Int) {
        // Crear un AlertDialog para confirmar la eliminación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta escuela?")

        // Configurar las acciones de los botones del AlertDialog
        builder.setPositiveButton("Sí") { dialog, which ->
            // Si el usuario confirma, proceder con la eliminación
            val call = apiService.inactivarEscuela(id)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EscuelaModificar, "Escuela eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EscuelaModificar, "Error al eliminar la escuela", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@EscuelaModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Si el usuario cancela, simplemente cerrar el diálogo
            dialog.dismiss()
        }

        // Mostrar el AlertDialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
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

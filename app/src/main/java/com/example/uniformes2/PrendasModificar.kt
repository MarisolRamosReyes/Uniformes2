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

class PrendasModificar : AppCompatActivity() {
    private lateinit var editTextTipo: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var btnUpdatePrenda: Button
    private lateinit var btnDeletePrenda: Button
    private lateinit var btnGetAllPrendas: Button
    private lateinit var apiService: ApiService
    private lateinit var editTextId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_prendas)

        editTextId = findViewById(R.id.editTextId)
        editTextTipo = findViewById(R.id.editTextTipo)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        btnUpdatePrenda = findViewById(R.id.btnUpdatePrenda)
        btnDeletePrenda = findViewById(R.id.btnDeletePrenda)
        btnGetAllPrendas = findViewById(R.id.btnGetAllPrendas)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.157:7231/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnUpdatePrenda.setOnClickListener {
            val idString = editTextId.text.toString()
            val tipo = editTextTipo.text.toString()
            val descripcion = editTextDescripcion.text.toString()

            // Validar ID
            val id = try {
                idString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this@PrendasModificar, "ID inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prendaObj = PrendasT(id, tipo, descripcion, 1)
            updatePrenda(id, prendaObj)
        }

        btnDeletePrenda.setOnClickListener {
            val idG = editTextId.text.toString().trim()
            if (idG.isNotEmpty()) {
                val id = idG.toIntOrNull()
                if (id != null) {
                    deletePrenda(id)
                } else {
                    Toast.makeText(this@PrendasModificar, "ID inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@PrendasModificar, "Por favor, ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }
        btnGetAllPrendas.setOnClickListener {
            val intent = Intent(this@PrendasModificar, MostrarPrendas::class.java)
            startActivity(intent)
        }
    }

    private fun updatePrenda(id: Int, prenda: PrendasT) {
        val call = apiService.updatePrenda(id, prenda)
        call.enqueue(object : Callback<PrendasT> {
            override fun onResponse(call: Call<PrendasT>, response: Response<PrendasT>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PrendasModificar, "Prenda modificada correctamente", Toast.LENGTH_SHORT).show()
                    editTextId.text.clear()
                    editTextTipo.text.clear()
                    editTextDescripcion.text.clear()
                } else {
                    Toast.makeText(this@PrendasModificar, "Error al modificar la prenda", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrendasT>, t: Throwable) {
                Toast.makeText(this@PrendasModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deletePrenda(id: Int) {
        val builder = AlertDialog.Builder(this@PrendasModificar)
        builder.setTitle("Eliminar Prenda")
        builder.setMessage("¿Estás seguro de que quieres eliminar esta prenda?")
        builder.setPositiveButton("Sí") { _, _ ->
            performDeletePrenda(id)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun performDeletePrenda(id: Int) {
        val call = apiService.inactivarPrenda(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PrendasModificar, "Prenda eliminada correctamente", Toast.LENGTH_SHORT).show()
                    editTextId.text.clear()
                } else {
                    Toast.makeText(this@PrendasModificar, "Error al eliminada la prenda", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@PrendasModificar, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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

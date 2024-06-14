package com.example.uniformes2

import com.google.gson.annotations.SerializedName

data class EscuelaT (
    @SerializedName("idSc")
    val idSc: Int,
    val name: String,
    val address: String,
    val phone: String,
    val status: Int
)
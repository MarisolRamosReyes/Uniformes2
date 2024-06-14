package com.example.uniformes2

import com.google.gson.annotations.SerializedName

data class PrendasT (
    @SerializedName("idG")
    val idG: Int,
    val type: String,
    val description: String,
    val Status: Int
)
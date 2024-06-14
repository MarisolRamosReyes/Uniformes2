package com.example.uniformes2

import com.google.gson.annotations.SerializedName

data class TallasT (
    @SerializedName("idT")
    val idT: Int,
    val size: String,
    val price: String,
    val status: Int
)
package com.adrien.blissfulcake.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 
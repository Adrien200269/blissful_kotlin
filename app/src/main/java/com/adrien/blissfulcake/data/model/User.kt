package com.adrien.blissfulcake.data.model

data class User(
    val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val phone: String = ""
) 
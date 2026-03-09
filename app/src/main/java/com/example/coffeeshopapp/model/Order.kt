package com.example.coffeeshopapp.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val createdAt: Timestamp? = null
)

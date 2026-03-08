package com.example.coffeeshopapp.model

import java.io.Serializable

data class CoffeeModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int
) : Serializable

data class CartItem(
    val coffee: CoffeeModel,
    var quantity: Int
)

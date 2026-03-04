package com.example.coffeeshopapp // Make sure this matches your actual package name

import java.io.Serializable

// The blueprint for a single coffee on the menu
// We use Serializable so we can pass this object between Activities easily
data class CoffeeModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int // This will hold the ID of the image in your drawable folder
) : Serializable

// The blueprint for an item sitting in the cart
data class CartItem(
    val coffee: CoffeeModel,
    var quantity: Int
)
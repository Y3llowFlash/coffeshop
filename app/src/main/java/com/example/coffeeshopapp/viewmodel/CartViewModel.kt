package com.example.coffeeshopapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.CartManager
import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.model.CoffeeModel

class CartViewModel : ViewModel() {
    fun getCartItems(): List<CartItem> = CartManager.getCartItems()

    fun addItem(coffee: CoffeeModel) {
        CartManager.addItem(coffee)
    }

    fun getTotalPrice(): Double = CartManager.getTotalPrice()

    fun clearCart() {
        CartManager.clearCart()
    }

    fun saveCart(context: Context) {
        CartManager.saveCart(context)
    }

    fun loadCart(context: Context) {
        CartManager.loadCart(context)
    }
}

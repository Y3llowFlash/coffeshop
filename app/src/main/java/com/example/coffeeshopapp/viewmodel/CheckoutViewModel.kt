package com.example.coffeeshopapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.repository.OrderRepository

class CheckoutViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {
    fun saveOrder(
        items: List<CartItem>,
        totalPrice: Double,
        onResult: (Result<Unit>) -> Unit
    ) {
        orderRepository.saveOrder(items, totalPrice, onResult)
    }
}

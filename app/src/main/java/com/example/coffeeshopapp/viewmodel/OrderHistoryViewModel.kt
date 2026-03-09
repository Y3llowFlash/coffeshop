package com.example.coffeeshopapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.model.Order
import com.example.coffeeshopapp.repository.OrderHistoryRepository
import com.google.firebase.auth.FirebaseAuth

class OrderHistoryViewModel(
    private val repository: OrderHistoryRepository = OrderHistoryRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    fun loadUserOrders(onResult: (Result<List<Order>>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.success(emptyList()))
            return
        }

        repository.getOrdersForUser(userId, onResult)
    }
}

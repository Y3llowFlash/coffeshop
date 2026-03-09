package com.example.coffeeshopapp.repository

import com.example.coffeeshopapp.model.Order
import com.example.coffeeshopapp.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderHistoryRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getOrdersForUser(
        userId: String,
        onResult: (Result<List<Order>>) -> Unit
    ) {
        firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val orders = snapshot.documents.map { document ->
                    val items = (document.get("items") as? List<Map<String, Any?>>)
                        .orEmpty()
                        .map { item ->
                            OrderItem(
                                name = item["name"] as? String ?: "",
                                price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                                quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                            )
                        }

                    Order(
                        id = document.id,
                        items = items,
                        totalPrice = document.getDouble("totalPrice") ?: 0.0,
                        createdAt = document.getTimestamp("createdAt")
                    )
                }

                onResult(Result.success(orders))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private companion object {
        const val ORDERS_COLLECTION = "orders"
    }
}

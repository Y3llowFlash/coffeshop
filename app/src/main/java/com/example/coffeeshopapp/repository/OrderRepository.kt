package com.example.coffeeshopapp.repository

import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun saveOrder(
        items: List<CartItem>,
        totalPrice: Double,
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: ANONYMOUS_USER_ID
        val today = LocalDate.now()

        val orderItems = items.map { cartItem ->
            OrderItem(
                name = cartItem.coffee.name,
                price = cartItem.coffee.price,
                quantity = cartItem.quantity
            )
        }

        val orderData = hashMapOf(
            "userId" to userId,
            "items" to orderItems.map { item ->
                hashMapOf(
                    "name" to item.name,
                    "price" to item.price,
                    "quantity" to item.quantity
                )
            },
            "totalPrice" to totalPrice,
            "createdAt" to FieldValue.serverTimestamp(),
            "dayKey" to today.format(DAY_FORMATTER),
            "monthKey" to today.format(MONTH_FORMATTER),
            "yearKey" to today.format(YEAR_FORMATTER)
        )

        firestore.collection(ORDERS_COLLECTION)
            .add(orderData)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private companion object {
        const val ANONYMOUS_USER_ID = "anonymous"
        const val ORDERS_COLLECTION = "orders"
        val DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val MONTH_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val YEAR_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
    }
}

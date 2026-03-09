package com.example.coffeeshopapp.repository

import com.example.coffeeshopapp.model.CoffeeModel
import com.google.firebase.firestore.FirebaseFirestore

class MenuRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMenu(
        userId: String,
        onResult: (Result<List<CoffeeModel>>) -> Unit
    ) {

        firestore.collection("menus")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->

                val items = snapshot.documents.map { doc ->

                    CoffeeModel(
                        id = doc.id.toIntOrNull() ?: doc.id.hashCode(),
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        types = doc.get("types") as? List<String> ?: emptyList(),
                        imageDrawable = doc.getString("imageDrawable") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                }

                onResult(Result.success(items))
            }
            .addOnFailureListener {
                onResult(Result.failure(it))
            }
    }
}

package com.example.coffeeshopapp.repository

import com.example.coffeeshopapp.model.MockData
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MenuInitializer {
    private val firestore = FirebaseFirestore.getInstance()

    fun initializeMenuIfNeeded(
        userId: String,
        onComplete: () -> Unit
    ) {
        val menuDocument = firestore.collection(MENUS_COLLECTION).document(userId)

        menuDocument.collection(ITEMS_COLLECTION)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onComplete()
                    return@addOnSuccessListener
                }

                val batch = firestore.batch()
                batch.set(menuDocument, mapOf("initialized" to true))

                MockData.menuList.forEach { item ->
                    val itemDocument = menuDocument
                        .collection(ITEMS_COLLECTION)
                        .document(item.id.toString())
                    val imageDrawable = if (item.imageResId != 0) {
                        FirebaseApp.getInstance()
                            .applicationContext
                            .resources
                            .getResourceEntryName(item.imageResId)
                    } else {
                        item.imageDrawable
                    }

                    batch.set(
                        itemDocument,
                        mapOf(
                            "name" to item.name,
                            "description" to item.description,
                            "price" to item.price,
                            "types" to item.types,
                            "imageDrawable" to imageDrawable,
                            "imageUrl" to "",
                            "enabled" to true
                        )
                    )
                }

                batch.commit()
                    .addOnSuccessListener {
                        onComplete()
                    }
            }
    }

    private companion object {
        const val MENUS_COLLECTION = "menus"
        const val ITEMS_COLLECTION = "items"
    }
}

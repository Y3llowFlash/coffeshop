package com.example.coffeeshopapp.repository

import android.net.Uri
import com.example.coffeeshopapp.model.CoffeeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MenuEditorRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun getMenuItems(
        userId: String,
        onResult: (Result<List<CoffeeModel>>) -> Unit
    ) {
        firestore.collection(MENUS_COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
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
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun saveMenuItem(
        userId: String,
        item: CoffeeModel,
        imageUri: Uri?,
        preserveExistingImage: Boolean,
        onResult: (Result<Unit>) -> Unit
    ) {
        if (imageUri == null) {
            upsertMenuItem(userId, item, null, preserveExistingImage, onResult)
            return
        }

        val imageRef = storage.reference
            .child(IMAGES_COLLECTION)
            .child(userId)
            .child("${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: IllegalStateException("Image upload failed.")
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                upsertMenuItem(userId, item, downloadUri.toString(), false, onResult)
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun deleteMenuItem(
        userId: String,
        itemId: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        firestore.collection(MENUS_COLLECTION)
            .document(userId)
            .collection(ITEMS_COLLECTION)
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private fun upsertMenuItem(
        userId: String,
        item: CoffeeModel,
        uploadedImageUrl: String?,
        preserveExistingImage: Boolean,
        onResult: (Result<Unit>) -> Unit
    ) {
        val itemId = item.id.toString()
        val itemData = mutableMapOf<String, Any>(
            "name" to item.name,
            "description" to item.description,
            "price" to item.price,
            "types" to item.types,
            "enabled" to true
        )

        if (!preserveExistingImage) {
            if (item.imageDrawable.isNotBlank()) {
                itemData["imageDrawable"] = item.imageDrawable
            }
            if (!uploadedImageUrl.isNullOrBlank()) {
                itemData["imageUrl"] = uploadedImageUrl
            } else if (item.imageUrl.isNotBlank()) {
                itemData["imageUrl"] = item.imageUrl
            }
        }

        firestore.collection(MENUS_COLLECTION)
            .document(userId)
            .set(mapOf("initialized" to true))
            .continueWithTask {
                firestore.collection(MENUS_COLLECTION)
                    .document(userId)
                    .collection(ITEMS_COLLECTION)
                    .document(itemId)
                    .set(itemData, SetOptions.merge())
            }
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private companion object {
        const val MENUS_COLLECTION = "menus"
        const val ITEMS_COLLECTION = "items"
        const val IMAGES_COLLECTION = "menu_images"
    }
}

package com.example.coffeeshopapp.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun signInWithGoogle(
        idToken: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user == null) {
                    onResult(Result.failure(IllegalStateException("Authenticated user is null.")))
                    return@addOnSuccessListener
                }

                saveUserToFirestore(user) { firestoreResult ->
                    if (firestoreResult.isSuccess) {
                        onResult(Result.success(user))
                    } else {
                        onResult(
                            Result.failure(
                                firestoreResult.exceptionOrNull()
                                    ?: Exception("Failed to store user data.")
                            )
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    private fun saveUserToFirestore(
        user: FirebaseUser,
        onComplete: (Result<Unit>) -> Unit
    ) {
        val userData = hashMapOf<String, Any?>(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to user.displayName,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val existingCreatedAt = document.getTimestamp("createdAt") ?: Timestamp.now()
                    userData["createdAt"] = existingCreatedAt
                }

                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        onComplete(Result.success(Unit))
                    }
                    .addOnFailureListener { exception ->
                        onComplete(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(Result.failure(exception))
            }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}

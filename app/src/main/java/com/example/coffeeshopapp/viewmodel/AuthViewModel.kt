package com.example.coffeeshopapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    fun signInWithGoogle(
        idToken: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ) {
        repository.signInWithGoogle(idToken, onResult)
    }

    fun signOut() {
        repository.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = repository.getCurrentUser()
}

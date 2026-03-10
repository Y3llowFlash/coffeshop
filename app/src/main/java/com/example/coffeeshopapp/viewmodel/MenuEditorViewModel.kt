package com.example.coffeeshopapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.repository.MenuEditorRepository

class MenuEditorViewModel(
    private val repository: MenuEditorRepository = MenuEditorRepository()
) : ViewModel() {
    fun loadMenuItems(
        userId: String,
        onResult: (Result<List<CoffeeModel>>) -> Unit
    ) {
        repository.getMenuItems(userId, onResult)
    }

    fun saveMenuItem(
        userId: String,
        item: CoffeeModel,
        imageUri: Uri?,
        preserveExistingImage: Boolean,
        onResult: (Result<Unit>) -> Unit
    ) {
        repository.saveMenuItem(userId, item, imageUri, preserveExistingImage, onResult)
    }

    fun deleteMenuItem(
        userId: String,
        itemId: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        repository.deleteMenuItem(userId, itemId, onResult)
    }
}

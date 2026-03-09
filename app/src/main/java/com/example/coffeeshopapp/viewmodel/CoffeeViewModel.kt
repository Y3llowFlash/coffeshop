package com.example.coffeeshopapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.model.MockData
import com.example.coffeeshopapp.repository.CoffeeRepository
import com.example.coffeeshopapp.repository.MenuInitializer
import com.example.coffeeshopapp.repository.MenuRepository
import com.google.firebase.auth.FirebaseAuth

class CoffeeViewModel(
    private val repository: CoffeeRepository = CoffeeRepository()
) : ViewModel() {
    private val menuRepository = MenuRepository()
    private val menuInitializer = MenuInitializer()

    fun getCoffeeList(): List<CoffeeModel> = repository.getCoffeeList()

    fun getCoffeeById(id: Int): CoffeeModel? = repository.getCoffeeById(id)

    fun loadMenu(context: Context, onResult: (List<CoffeeModel>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onResult(MockData.menuList)
            return
        }

        val userId = currentUser.uid
        menuInitializer.initializeMenuIfNeeded(userId) {
            menuRepository.getMenu(userId) { result ->
                val items = result.getOrNull().orEmpty()

                if (items.isNotEmpty()) {
                    onResult(items)
                } else {
                    onResult(MockData.menuList)
                }
            }
        }
    }
}

package com.example.coffeeshopapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.repository.CoffeeRepository

class CoffeeViewModel(
    private val repository: CoffeeRepository = CoffeeRepository()
) : ViewModel() {
    fun getCoffeeList(): List<CoffeeModel> = repository.getCoffeeList()

    fun getCoffeeById(id: Int): CoffeeModel? = repository.getCoffeeById(id)
}
